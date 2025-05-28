package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import org.gradle.ai.nlp.server.MCPServerApplication;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class MCPServerService implements BuildService<MCPServerService.@NotNull Params>, AutoCloseable {
    public static final String SERVER_STARTUP_MESSAGE = "Started MCP Server";
    public static final String SERVER_SHUTDOWN_MESSAGE = "Shutdown MCP Server";

    private final Logger logger = Logging.getLogger(MCPServerService.class);

    private ConfigurableApplicationContext serverContext;

    public boolean isStarted() {
        return serverContext != null && serverContext.isActive();
    }

    public void startServer() {
        Preconditions.checkState(!isStarted(), "MCP Server already started");

        serverContext = MCPServerApplication.run(new String[]{ "--server.port=" + getParameters().getPort().get() });
        logger.lifecycle(SERVER_STARTUP_MESSAGE);
    }

    @Override
    public void close() {
        if (serverContext != null && !serverContext.isClosed()) {
            serverContext.close();
            logger.lifecycle(SERVER_SHUTDOWN_MESSAGE);
        }
    }

    public interface Params extends BuildServiceParameters {
        Property<Integer> getPort();
        RegularFileProperty getTasksReportFile();
        RegularFileProperty getLogFile();
    }
}
