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
import org.springframework.boot.web.reactive.context.ConfigurableReactiveWebApplicationContext;

import java.io.Closeable;

public abstract class MCPServerService implements BuildService<MCPServerService.@NotNull Params>, Closeable {
    public static final String SERVER_STARTUP_MESSAGE = "Started MCP Server";
    public static final String SERVER_SHUTDOWN_MESSAGE = "Shutdown MCP Server";

    private final Logger logger = Logging.getLogger(MCPServerService.class);

    private ConfigurableReactiveWebApplicationContext serverContext;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isStarted() {
        return serverContext != null && serverContext.isActive();
    }

    public void startServer() {
        Preconditions.checkState(!isStarted(), "MCP Server already started");
        Preconditions.checkState(getParameters().getAnthropicApiKey().isPresent(), "Anthropic API key is not available in the service parameters - was it set in the plugin extension or via the ANTHROPIC_API_KEY environment variable?");

        serverContext = (ConfigurableReactiveWebApplicationContext) MCPServerApplication.run(
                getParameters().getPort().get(),
                getParameters().getAnthropicApiKey().get(),
                getParameters().getLogFile().getAsFile().get(),
                getParameters().getTasksReportFile().getAsFile().get(),
                getParameters().getGradleFilesReportFile().getAsFile().get()
        );
        logger.lifecycle(SERVER_STARTUP_MESSAGE);
    }

    @Override
    public void close() {
        if (serverContext != null && !serverContext.isClosed()) {
            logger.lifecycle("Closing MCP Server...");
            serverContext.stop();
            serverContext.close();
            logger.lifecycle(SERVER_SHUTDOWN_MESSAGE);
        }
    }

    public interface Params extends BuildServiceParameters {
        Property<Integer> getPort();
        Property<String> getAnthropicApiKey();
        RegularFileProperty getLogFile();

        RegularFileProperty getTasksReportFile();
        RegularFileProperty getGradleFilesReportFile();
    }
}
