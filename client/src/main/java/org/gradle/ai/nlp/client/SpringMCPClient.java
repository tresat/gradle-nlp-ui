package org.gradle.ai.nlp.client;

import com.google.common.annotations.VisibleForTesting;
import io.modelcontextprotocol.client.McpSyncClient;
import org.gradle.ai.nlp.exception.MissingRequiredPropertiesException;
import org.gradle.ai.nlp.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class SpringMCPClient {
    public static final String CONFIG_NAME_PROPERTY = "spring.config.name";
    public static final String ANTHROPIC_API_KEY_PROPERTY = Util.ANTHROPIC_API_KEY_PROPERTY;
    public static final List<String> REQUIRED_PROPERTIES = List.of(ANTHROPIC_API_KEY_PROPERTY);

    private static final Logger logger = LoggerFactory.getLogger(SpringMCPClient.class);

    public static void main(String[] args) {
        run(args);
    }

    public static ConfigurableApplicationContext run(String anthropicApiKey) {
        return run(anthropicApiKey, Collections.emptyList());
    }

    public static ConfigurableApplicationContext run(String anthropicApiKey, List<String> mcpServerUrls) {
        String[] args = new String[]{
                "--" + CONFIG_NAME_PROPERTY + "=application-client",
                "--spring.config.location=", // This is to ensure that the application does not read from the default application.properties used by the server
                "--" + ANTHROPIC_API_KEY_PROPERTY + "=" + anthropicApiKey,
        };
        String[] argsWithServers = addServersToArgs(args, mcpServerUrls);
        return run(argsWithServers);
    }

    private static String[] addServersToArgs(String[] args, List<String> mcpServerUrls) {
        String[] result = new String[args.length + mcpServerUrls.size()];
        System.arraycopy(args, 0, result, 0, args.length);

        for (int i = 0; i < mcpServerUrls.size(); i++) {
            String newServer = "--spring.ai.mcp.client.sse.connections.server" + i + ".url=" + mcpServerUrls.get(i);
            result[args.length + i] = newServer;
            System.out.println("Adding MCP server URL to client properties: " + newServer);
        }

        return result;
    }

    private static ConfigurableApplicationContext run(String[] args) {
        verifyArgs(args);
        return new SpringApplicationBuilder(SpringMCPClient.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @VisibleForTesting
    static void verifyArgs(String[] args) {
        if (args == null || args.length < REQUIRED_PROPERTIES.size()) {
            throw new MissingRequiredPropertiesException(REQUIRED_PROPERTIES, args);
        }

        for (String requiredArg : REQUIRED_PROPERTIES) {
            boolean found = false;
            for (String arg : args) {
                if (arg.startsWith("--" + requiredArg + "=")) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new MissingRequiredPropertiesException(REQUIRED_PROPERTIES, args);
            }
        }
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

