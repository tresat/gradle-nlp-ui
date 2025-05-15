/*
 * Copyright 2025-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.nlp.client;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.model.tool.autoconfigure.ToolCallingAutoConfiguration;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner chatbot(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients) {
        ToolCallingAutoConfiguration c = new ToolCallingAutoConfiguration();

        return args -> {

            var chatClient = chatClientBuilder
                    .defaultSystem("You are useful assistant and can perform web searches Brave's search API to reply to your questions.")
                    .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
                    .defaultAdvisors(new MessageChatMemoryAdvisor(MessageWindowChatMemory.builder().build()))
                    .build();

            // Start the chat loop
            System.out.println("\nI am your AI assistant.\n");

            String response = chatClient.prompt("Who was the first person to climb Mount Everest?")
                                    .call()
                                    .content();
            System.out.println(response);

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
