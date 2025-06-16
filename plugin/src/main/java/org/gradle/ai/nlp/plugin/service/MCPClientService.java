package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import org.gradle.ai.nlp.client.MCPClient;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public abstract class MCPClientService implements BuildService<MCPClientService.@NotNull Params>, Closeable {
    public static final String CLIENT_STARTUP_MESSAGE = "Started MCP Client";
    public static final String CLIENT_SHUTDOWN_MESSAGE = "Shutdown MCP Client";

    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server from the Client Service: '{}'%n";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server in the Client Service: '{}'%n";

    private static final Logger logger = LoggerFactory.getLogger(MCPClientService.class);

    private final MCPClient mcpClient = new MCPClient();

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

        logger.info(QUERYING_MSG_TEMPLATE, query);
        var answer = mcpClient.query(query);
        logger.info(ANSWER_MSG_TEMPLATE, answer);

        return answer;
    }

    @Override
    public void close() {
        if (isConnected()) {
            mcpClient.close();
            logger.info(CLIENT_SHUTDOWN_MESSAGE);
        }
    }

    public interface Params extends BuildServiceParameters {
        Property<String> getAnthropicApiKey();
        ListProperty<String> getServerUrls();
    }
}
