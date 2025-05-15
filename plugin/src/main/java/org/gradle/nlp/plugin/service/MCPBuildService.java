package org.gradle.nlp.plugin.service;

import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.springframework.ai.mcp.sample.server.McpServerApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class MCPBuildService implements BuildService<BuildServiceParameters.None>, AutoCloseable {
    private ConfigurableApplicationContext context;

    public MCPBuildService() {
        System.out.println("Starting MCP Server");
    }

    public void useServer() {
        context = SpringApplication.run(McpServerApplication.class);
        System.out.println("Using MCP Server");
    }

    @Override
    public void close() {
        System.out.println("Shutting down MCP Server");
        if (context != null) {
            context.close();
        }
        System.out.println("MCP Server shut down");
    }
}
