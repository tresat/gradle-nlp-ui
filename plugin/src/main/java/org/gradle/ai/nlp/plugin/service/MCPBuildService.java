package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.springframework.ai.mcp.sample.server.McpServerApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import org.gradle.api.logging.Logger;

public abstract class MCPBuildService implements BuildService<BuildServiceParameters.None>, AutoCloseable {
    public static final String SERVER_STARTUP_MESSAGE = "Started MCP Server";
    public static final String SERVER_SHUTDOWN_MESSAGE = "Shutdown MCP Server";

    private ConfigurableApplicationContext context;
    private Logger logger;

    public boolean isStarted() {
        return context != null;
    }

    public void setLogger(Logger logger) {
        Preconditions.checkState(!isStarted(), "MCP Server already started");
        this.logger = logger;
    }

    public void startServer() {
        Preconditions.checkState(!isStarted(), "MCP Server already started");
        Preconditions.checkState(logger != null, "Logger not set");

        context = SpringApplication.run(McpServerApplication.class);
        logger.lifecycle(SERVER_STARTUP_MESSAGE);
    }

    public String query(String query) {
        return "42";
    }

    @Override
    public void close() {
        if (context != null) {
            context.close();
            logger.lifecycle(SERVER_SHUTDOWN_MESSAGE);
        }
    }
}
