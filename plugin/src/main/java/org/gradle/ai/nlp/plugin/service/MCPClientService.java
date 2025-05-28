package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MCPClientService implements BuildService<BuildServiceParameters.None>, AutoCloseable {
    public static final String SERVER_URL = "http://localhost:8080/sse"; // TODO: inject URL into constructor

    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server: '%s'%n";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server: '%s'%n";

    private final Logger logger = LoggerFactory.getLogger(MCPClientService.class);
    private final McpSyncClient mcpClient;

    public MCPClientService() {
        var sseTransport = HttpClientSseClientTransport.builder(SERVER_URL).build();
        mcpClient = McpClient.sync(sseTransport).build();
    }
    public boolean isConnected() {
        return mcpClient.isInitialized();
    }

    public void connect() {
        Preconditions.checkState(!isConnected(), "Already connected");
        mcpClient.initialize();
    }

    public String query(String query) {
//        logger.info(String.format(QUERYING_MSG_TEMPLATE, query));
//        String answer = "42"; // Simulated response from the MCP server
//        logger.info(String.format(ANSWER_MSG_TEMPLATE, answer));
//        return answer;

        McpSchema.ListToolsResult toolsList = mcpClient.listTools();

        System.out.println();
        System.out.println("Tools available:");
        System.out.println();
        toolsList.tools().forEach(t -> {
            System.out.println("Tool: " + t.name());
            System.out.println("Description: " + t.description());
            System.out.println();
        });

        return "43";
    }

    @Override
    public void close() throws Exception {
        logger.info("Closing MCP Client Service...");
        if (isConnected()) {
            mcpClient.close();
        }
        logger.info("Closed the MCP Client Service.");
    }
}
