package org.gradle.ai.nlp.client;

import com.google.common.base.Preconditions;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;

public class MCPClient implements AutoCloseable{
    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server: '%s'%n";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server: '%s'%n";

    public static final String SERVER_URL = "http://localhost:8080"; // Default for Spring Boot

    private McpSyncClient mcpClient;

    public MCPClient() {
        var sseTransport = HttpClientSseClientTransport.builder(SERVER_URL).build();
        mcpClient = McpClient.sync(sseTransport).build();
        mcpClient.initialize();
    }

    public String query(String query) {
        Preconditions.checkState(mcpClient != null && mcpClient.isInitialized(), "MCP Client not initialized");

        String answer = "42"; // Simulated response from the MCP server
        return answer;
    }

    @Override
    public void close() throws Exception {
        if (mcpClient != null) {
            mcpClient.closeGracefully();
        }
    }
}
