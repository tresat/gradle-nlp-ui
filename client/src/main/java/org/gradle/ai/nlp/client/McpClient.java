package org.gradle.ai.nlp.client;

import com.google.common.base.Preconditions;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpClient implements AutoCloseable{
    private final Logger logger = LoggerFactory.getLogger(McpClient.class);

    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server: '%s'%n";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server: '%s'%n";

    private final McpSyncClient mcpClient;

    public McpClient(String serverBaseUrl) {
        var serverUrl = serverBaseUrl + "/sse";
        var sseTransport = HttpClientSseClientTransport.builder(serverUrl).build();
        mcpClient = io.modelcontextprotocol.client.McpClient.sync(sseTransport).build();
    }

    public void connect() {
        mcpClient.initialize();
    }

    public boolean isConnected() {
        return mcpClient.isInitialized();
    }

    public String query(String query) {
        Preconditions.checkState(isConnected(), "Client not connected");

        logger.info(String.format(QUERYING_MSG_TEMPLATE, query));
        String answer = "42"; // Simulated response from the MCP server
        logger.info(String.format(ANSWER_MSG_TEMPLATE, answer));

        return answer;
    }

    @Override
    public void close() {
        if (isConnected()) {
            mcpClient.closeGracefully();
            logger.info("Client closed gracefully.");
        }
    }
}
