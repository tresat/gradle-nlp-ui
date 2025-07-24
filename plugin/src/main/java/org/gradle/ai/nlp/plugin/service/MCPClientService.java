package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import org.gradle.ai.nlp.client.MCPClient;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.jetbrains.annotations.NotNull;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.Closeable;

public abstract class MCPClientService implements BuildService<MCPClientService.@NotNull Params>, Closeable {
    public static final String CLIENT_STARTUP_MESSAGE = "Started MCP Client";
    public static final String CLIENT_SHUTDOWN_MESSAGE = "Shutdown MCP Client";

    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server from the Client Service: '{}'";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server in the Client Service: '{}'";

    private final Logger logger = Logging.getLogger(MCPClientService.class);

    private final MCPClient mcpClient = new MCPClient(logger);

    public boolean isConnected() {
        return mcpClient.isConnected();
    }

    public void connect() {
        Preconditions.checkState(!isConnected(), "Already connected!");
        Preconditions.checkState(getParameters().getAnthropicApiKey().isPresent(), "Anthropic API key is not available in the service parameters - was it set in the plugin extension or via the ANTHROPIC_API_KEY environment variable?");

        mcpClient.connect(getParameters().getAnthropicApiKey().get(), getParameters().getServerUrls().get());
        logger.info(CLIENT_STARTUP_MESSAGE);
    }

    public String query(String query) {
        Preconditions.checkState(isConnected(), "Client not connected");

        logger.lifecycle(QUERYING_MSG_TEMPLATE, query);
        var answer = mcpClient.query(query);
        logger.lifecycle(ANSWER_MSG_TEMPLATE, answer);

        return answer;
    }

    @Override
    public void close() {
        if (isConnected()) {
            logger.lifecycle("Closing MCP Client...");
            mcpClient.close();
            logger.lifecycle(CLIENT_SHUTDOWN_MESSAGE);
        }
    }

    public interface Params extends BuildServiceParameters {
        Property<@NotNull String> getAnthropicApiKey();
        ListProperty<@NotNull String> getServerUrls();
    }
}
