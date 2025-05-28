package org.gradle.ai.nlp.client;

import com.google.common.base.Preconditions;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCPClient implements AutoCloseable{
    private final Logger logger = LoggerFactory.getLogger(MCPClient.class);

    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server: '%s'%n";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server: '%s'%n";

    public static final String SERVER_URL = "http://localhost:8080/sse"; // TODO: inject URL into constructor

    private final McpSyncClient mcpClient;

    public MCPClient() {
        var sseTransport = HttpClientSseClientTransport.builder(SERVER_URL).build();
        mcpClient = McpClient.sync(sseTransport).build();
    }

    public void connect() {
        mcpClient.initialize();
    }

    public boolean isConnected() {
        return mcpClient.isInitialized();
    }

    public String query(String query) {
        Preconditions.checkState(isConnected(), "MCP Client not connected");

        logger.info(String.format(QUERYING_MSG_TEMPLATE, query));
        String answer = "42"; // Simulated response from the MCP server
        logger.info(String.format(ANSWER_MSG_TEMPLATE, answer));

        return answer;
    }

    @Override
    public void close() {
        if (isConnected()) {
            mcpClient.closeGracefully();
            logger.info("MCP Client closed gracefully.");
        }
    }
}
