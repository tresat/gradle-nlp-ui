package org.gradle.ai.nlp.server;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;

@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {
    private final Environment environment;

    public StartupListener(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String port = environment.getProperty("local.server.port", "8080");
        String sseEndpoint = environment.getProperty("spring.ai.mcp.server.sse-endpoint", "/sse");
        String url = "http://localhost:" + port + sseEndpoint;

        if (event.getApplicationContext().isRunning()) {
            System.out.println("MCP server is listening on: " + url);
        }

        String propertyValue = environment.getProperty("spring.ai.mcp.client.enabled");
        if (propertyValue != null) {
            throw new IllegalStateException("MCP Server is reading client properties!");
        }

        String pathToTasksReport = environment.getProperty("org.gradle.ai.nlp.server.tasks.report.file");
        if (pathToTasksReport == null || pathToTasksReport.isEmpty()) {
            throw new IllegalStateException("Property 'org.gradle.ai.nlp.server.tasks.report.file' is not set.");
        }

        File pathsReportFile = new File(pathToTasksReport);
        if (pathsReportFile.exists()) {
            System.out.println("Path to tasks report: " + pathsReportFile.getAbsolutePath());
        } else {
            throw new IllegalStateException("Tasks report file does not exist: " + pathsReportFile.getAbsolutePath());
        }
    }
}
