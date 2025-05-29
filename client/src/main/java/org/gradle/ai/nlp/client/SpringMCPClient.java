package org.gradle.ai.nlp.client;

import io.modelcontextprotocol.client.McpSyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@SpringBootApplication
public class SpringMCPClient {
    public static final String API_KEYS_FILE = "api-keys.properties";
    public static final String ANTHROPIC_API_KEY_PROPERTY = "spring.ai.anthropic.api-key";

    private static final Logger logger = LoggerFactory.getLogger(SpringMCPClient.class);

    public static ConfigurableApplicationContext run() {
        return run(new String[]{});
    }

    public static ConfigurableApplicationContext run(String[] args) {
        var anthropicApiKey = readAnthropicApiKeyFromProperties();
        String[] updatedArgs = addAnthropicKeyToArgs(args, anthropicApiKey);

        return SpringApplication.run(SpringMCPClient.class, updatedArgs);
    }

    private static String[] addAnthropicKeyToArgs(String[] args, String anthropicApiKey) {
        logger.info("ANTHROPIC_API_KEY: {}", anthropicApiKey);

        String[] updatedArgs = new String[args.length + 1];
        System.arraycopy(args, 0, updatedArgs, 0, args.length);
        updatedArgs[args.length] = "--" + ANTHROPIC_API_KEY_PROPERTY + "=" + anthropicApiKey;
        return updatedArgs;
    }

    // TODO: Get the API key from environment variable?  Additional properties profile?
    public static String readAnthropicApiKeyFromProperties() {
        Properties apiKeysProperties = new Properties();
        try (var propStream = SpringMCPClient.class.getClassLoader().getResourceAsStream(API_KEYS_FILE)) {
            if (propStream == null) {
                throw new IllegalStateException(String.format("Properties file '%s' not found in classpath!", API_KEYS_FILE));
            }
            apiKeysProperties.load(propStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!apiKeysProperties.containsKey(ANTHROPIC_API_KEY_PROPERTY)) {
            throw new IllegalStateException(String.format("Property '%s' not found in '%s'!", ANTHROPIC_API_KEY_PROPERTY, API_KEYS_FILE));
        }

        return (String) apiKeysProperties.get(ANTHROPIC_API_KEY_PROPERTY);
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients) {
        return chatClientBuilder
                .defaultSystem("You are a Gradle expert.  You can answer questions about the Gradle build with the given tasks provided by the build-mcp-server.")
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .build();
    }

    private void logMCPConnections(List<McpSyncClient> mcpSyncClients) {
        var clientsMessage = "There are " + mcpSyncClients.size() + " clients: " + mcpSyncClients.stream()
                .map(McpSyncClient::getServerInfo)
                .map(Record::toString)
                .collect(Collectors.joining(", "));
        logger.info(clientsMessage);
    }
}