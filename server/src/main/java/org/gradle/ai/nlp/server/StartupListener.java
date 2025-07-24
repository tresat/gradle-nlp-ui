package org.gradle.ai.nlp.server;

import org.gradle.ai.nlp.util.ServerKeys;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {
    private final Environment environment;

    public StartupListener(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String port = environment.getProperty(ServerKeys.SERVER_PORT_PROPERTY, "8080");
        String sseEndpoint = environment.getProperty("spring.ai.mcp.server.sse-endpoint", "/sse");
        String url = "http://localhost:" + port + sseEndpoint;

        if (event.getApplicationContext().isRunning()) {
            System.out.println(ServerKeys.LISTENING_ON + url);
        }

        String propertyValue = environment.getProperty("spring.ai.mcp.client.enabled");
        if (propertyValue != null) {
            throw new IllegalStateException("MCP Server is reading client properties!");
        }

        String pathToTasksReport = environment.getProperty(ServerKeys.TASKS_REPORT_FILE_PROPERTY);
        if (pathToTasksReport == null || pathToTasksReport.isEmpty()) {
            throw new IllegalStateException("Property '" + ServerKeys.TASKS_REPORT_FILE_PROPERTY + "' is not set.");
        }

        File pathsReportFile = new File(pathToTasksReport);
        if (!pathsReportFile.exists()) {
            throw new IllegalStateException("Tasks report file does not exist: " + pathsReportFile.getAbsolutePath());
        }

        String pathToLogFile = environment.getProperty(ServerKeys.LOG_FILE_PROPERTY);
        if (pathToLogFile == null || pathToLogFile.isEmpty()) {
            throw new IllegalStateException("Property '" + ServerKeys.LOG_FILE_PROPERTY + "' is not set.");
        }

        File logFile = new File(pathToLogFile);
        System.out.println("Path to log file: " + logFile.getAbsolutePath());
    }
}
