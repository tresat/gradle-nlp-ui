package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import org.gradle.ai.nlp.server.McpServerApplication;
import org.gradle.api.logging.Logging;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import org.gradle.api.logging.Logger;

public abstract class MCPServerService implements BuildService<BuildServiceParameters.@NotNull None>, AutoCloseable {
    public static final String SERVER_STARTUP_MESSAGE = "Started MCP Server";
    public static final String SERVER_SHUTDOWN_MESSAGE = "Shutdown MCP Server";

    private final Logger logger = Logging.getLogger(MCPClientService.class);

    private ConfigurableApplicationContext serverContext;

    public boolean isStarted() {
        return serverContext != null && serverContext.isActive();
    }

    public void startServer() {
        Preconditions.checkState(!isStarted(), "MCP Server already started");

        serverContext = SpringApplication.run(McpServerApplication.class);
        logger.lifecycle(SERVER_STARTUP_MESSAGE);
    }

    @Override
    public void close() {
        if (serverContext != null && !serverContext.isClosed()) {
            serverContext.close();
            logger.lifecycle(SERVER_SHUTDOWN_MESSAGE);
        }
    }
}
