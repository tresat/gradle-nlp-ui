package org.gradle.ai.nlp.server;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class MCPServerApplication {
    public static void main(String[] args) {
        run(args);
    }

    public static ConfigurableApplicationContext run(int port, File tasksReportFile) {
        var args = new String[]{
                "--server.port=" + port,
                "--org.gradle.ai.nlp.server.tasks.report.file=" + tasksReportFile.getAbsolutePath()
        };
        return run(args);
    }

    public static ConfigurableApplicationContext run(String[] args) {
        return SpringApplication.run(MCPServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider tasksInfoTool() {
        return MethodToolCallbackProvider.builder().toolObjects(new TasksInfoTool()).build();
    }
}
