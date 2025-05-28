package org.gradle.ai.nlp.server;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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

        String pathToTasksReport = environment.getProperty("org.gradle.ai.nlp.server.tasks.report.file");
        System.out.println("Path to tasks report: " + pathToTasksReport);
    }
}
