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

import java.util.List;
import java.util.stream.Collectors;

import static org.gradle.ai.nlp.util.Util.ANTHROPIC_API_KEY_PROPERTY;

@SpringBootApplication
public class SpringMCPClient {
    private static final Logger logger = LoggerFactory.getLogger(SpringMCPClient.class);

    public static ConfigurableApplicationContext run(String anthropicApiKey) {
        String[] args = new String[]{
                "--spring.config.name=application-client",
                "--" + ANTHROPIC_API_KEY_PROPERTY + "=" + anthropicApiKey,
        };
        return SpringApplication.run(SpringMCPClient.class, args);
    }

//    public static ConfigurableApplicationContext run(String[] args) {
//        var anthropicApiKey = readAnthropicApiKeyFromProperties();
//        String[] updatedArgs = addAnthropicKeyToArgs(args, anthropicApiKey);
//        updatedArgs = addApplicationClientPropertiesArg(updatedArgs);
//
//        return SpringApplication.run(SpringMCPClient.class, updatedArgs);
//    }

    private static String[] addAnthropicKeyToArgs(String[] args, String anthropicApiKey) {
        logger.info("ANTHROPIC_API_KEY: {}", anthropicApiKey);

        String[] updatedArgs = new String[args.length + 1];
        System.arraycopy(args, 0, updatedArgs, 0, args.length);
        updatedArgs[args.length] = "--" + ANTHROPIC_API_KEY_PROPERTY + "=" + anthropicApiKey;
        return updatedArgs;
    }

    private static String[] addApplicationClientPropertiesArg(String[] args) {
        String[] updatedArgs = new String[args.length + 1];
        System.arraycopy(args, 0, updatedArgs, 0, args.length);
        updatedArgs[args.length] = "--spring.config.name=application-client";
        return updatedArgs;
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients) {
        return chatClientBuilder
                .defaultSystem("You are a Gradle expert.  Always use the TasksInfoTool available on the MCP server to answer task related questions about a specific Gradle build.  This tool provides the output of a tasks report for this specific Gradle build and describes all the available tasks in it.  Use this information to answer task related questions.  When asked for a specific task, provide the description as well, in the format: 'taskName - taskDescription'.")
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

