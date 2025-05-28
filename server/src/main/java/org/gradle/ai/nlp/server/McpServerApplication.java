package org.gradle.ai.nlp.server;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class McpServerApplication {
    public static void main(String[] args) {
        run(args);
    }

    public static ConfigurableApplicationContext run(String[] args) {
        return SpringApplication.run(McpServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider tasksInfoTool() {
        return MethodToolCallbackProvider.builder().toolObjects(new TasksInfoTool()).build();
    }
}
