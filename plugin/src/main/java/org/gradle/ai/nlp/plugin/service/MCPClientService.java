package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MCPClientService implements BuildService<MCPClientService.@NotNull Params>, AutoCloseable {
    public static final String CLIENT_STARTUP_MESSAGE = "Started MCP Client";
    public static final String CLIENT_SHUTDOWN_MESSAGE = "Shutdown MCP Client";

    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server from the Client Service: '{}'%n";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server in the Client Service: '{}'%n";

    private static final Logger logger = LoggerFactory.getLogger(MCPClientService.class);

    private final McpSyncClient mcpClient;

    public MCPClientService() {
        var serverUrl = String.format("http://localhost:%d/%s", + getParameters().getPort().get(), "sse");

        var sseTransport = HttpClientSseClientTransport.builder(serverUrl).build();
        mcpClient = McpClient.sync(sseTransport).build();
    }
    public boolean isConnected() {
        return mcpClient.isInitialized();
    }

    public void connect() {
        Preconditions.checkState(!isConnected(), "Already connected!");
        mcpClient.initialize();
        logger.info(CLIENT_STARTUP_MESSAGE);
    }

    public String query(String query) {
        logger.info(QUERYING_MSG_TEMPLATE, query);

        McpSchema.ListToolsResult toolsList = mcpClient.listTools();
        System.out.println();
        System.out.println("Tools available:");
        toolsList.tools().forEach(t -> {
            System.out.println("\tTool: " + t.name());
            System.out.println("\tDescription: " + t.description());
        });
        System.out.println();

        String answer = "42"; // Simulated response from the MCP server

        logger.info(ANSWER_MSG_TEMPLATE, answer);

        return answer;
    }

    @Override
    public void close() {
        if (isConnected()) {
            mcpClient.closeGracefully();
            logger.info(CLIENT_SHUTDOWN_MESSAGE);
        }
    }

    public interface Params extends BuildServiceParameters {
        Property<Integer> getPort();
    }
}
