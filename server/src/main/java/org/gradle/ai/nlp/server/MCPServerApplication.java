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
    public static final String SERVER_PORT_PROPERTY = "server.port";
    public static final String TASKS_REPORT_FILE_PROPERTY = "org.gradle.ai.nlp.server.tasks.report.file";

    public static void main(String[] args) {
        run(args);
    }

    public static ConfigurableApplicationContext run(int port, File tasksReportFile) {
        var args = new String[]{
                "--" + SERVER_PORT_PROPERTY + "=" + port,
                "--" + TASKS_REPORT_FILE_PROPERTY + "=" + tasksReportFile.getAbsolutePath()
        };
        return run(args);
    }

    public static ConfigurableApplicationContext run(String[] args) {
        verifyArgs(args);
        return SpringApplication.run(MCPServerApplication.class, args);
    }

    private static void verifyArgs(String[] args) {
        // Verify that the server.port and org.gradle.ai.nlp.server.tasks.report.file properties are set
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("No arguments provided.  Please specify: " + SERVER_PORT_PROPERTY + " and: " + TASKS_REPORT_FILE_PROPERTY + ".");
        }

        boolean hasPort = false;
        boolean hasTasksReportFile = false;
        for (String arg : args) {
            if (arg.startsWith("--" + SERVER_PORT_PROPERTY + "=")) {
                hasPort = true;
            } else if (arg.startsWith("--" + TASKS_REPORT_FILE_PROPERTY + "=")) {
                hasTasksReportFile = true;
            }
        }

        if (!hasPort) {
            throw new IllegalArgumentException("Missing required argument: " + SERVER_PORT_PROPERTY);
        }
        if (!hasTasksReportFile) {
            throw new IllegalArgumentException("Missing required argument: " + TASKS_REPORT_FILE_PROPERTY);
        }
    }

    @Bean
    public ToolCallbackProvider tasksInfo(TasksInfoTool tasksInfoTool) {
        return MethodToolCallbackProvider.builder().toolObjects(tasksInfoTool).build();
    }
}
