package org.gradle.ai.nlp.client;

import io.modelcontextprotocol.client.McpSyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
public class SpringMCPClient {
    public static final String API_KEYS_FILE = "api-keys.properties";
    public static final String ANTHROPIC_API_KEY_PROPERTY = "spring.ai.anthropic.api-key";

    private static final Logger logger = LoggerFactory.getLogger(SpringMCPClient.class);

    public static void main(String[] args) {
        run(args);
    }

    public static ConfigurableApplicationContext run() {
        var args = new String[]{};
        return run(args);
    }

    public static ConfigurableApplicationContext run(String[] args) {
        var anthropicApiKey = readAnthropicApiKeyFromProperties();
        String[] updatedArgs = addAnthropicKeyToArgs(args, anthropicApiKey);

        return SpringApplication.run(SpringMCPClient.class, updatedArgs);
    }

    private static String[] addAnthropicKeyToArgs(String[] args, String anthropicApiKey) {
        logger.error("ANTHROPIC_API_KEY from env: {}", anthropicApiKey);

        String[] updatedArgs = new String[args.length + 1];
        System.arraycopy(args, 0, updatedArgs, 0, args.length);
        updatedArgs[args.length] = "--" + ANTHROPIC_API_KEY_PROPERTY + "=" + anthropicApiKey;
        return updatedArgs;
    }

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

//    @Value("${ai.user.input}")
//    private String userInput;

//    @Bean
//    public ChatClient anthropicChatClient(AnthropicChatModel chatModel, ToolCallbackProvider tools) {
//        return ChatClient.builder(chatModel)
//                .defaultToolCallbacks(tools)
//                .build();
//    }

//    @Bean
//    public CommandLineRunner predefinedQuestions(ChatClient chatClient,
//                                                 ConfigurableApplicationContext context) {
//        return args -> {
//            var userInput = "What is the weather in San Francisco?";
//
//            System.out.println("\n>>> QUESTION: " + userInput);
//            System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
//
//            context.close();
//        };
//    }

    @Bean
    public CommandLineRunner chatbot(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients) {

        return args -> {

            var chatClient = chatClientBuilder
                    .defaultSystem("You are useful assistant and can perform web searches Brave's search API to reply to your questions.")
                    .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
                    .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                    .build();

            // Start the chat loop
            System.out.println("\nI am your AI assistant.\n");

            var response = chatClient.prompt("Tell me a joke").call().content();
            System.out.println("Response: " + response);
//            try (Scanner scanner = new Scanner(System.in)) {
//                while (true) {
//                    System.out.print("\nUSER: ");
//                    System.out.println("\nASSISTANT: " +
//                            chatClient.prompt(scanner.nextLine()) // Get the user input
//                                    .call()
//                                    .content());
//                }
//            }

        };
    }
}