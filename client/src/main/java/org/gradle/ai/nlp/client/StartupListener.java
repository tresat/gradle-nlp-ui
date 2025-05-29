package org.gradle.ai.nlp.client;

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
        if (event.getApplicationContext().isRunning()) {
            System.out.println("MCP client is running.");
        }

        String propertyValue = environment.getProperty("spring.ai.mcp.server.enabled");
        if (propertyValue != null) {
            throw new IllegalStateException("MCP Client is reading server properties!");
        }

        for (int i = 0; i<10; i++) {
            String serverUrl = environment.getProperty("spring.ai.mcp.client.sse.connections.server" + i + ".url");
            if (serverUrl != null) {
                System.out.println("MCP client knows about server: " + serverUrl);
            } else {
                break; // No more servers configured
            }
        }
    }
}
