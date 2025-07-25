package org.gradle.ai.nlp.server

import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.McpSyncClient
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport

abstract class AbstractMCPServerApplicationWithSyncClientFunctionalTest extends AbstractMCPServerApplicationFunctionalTest {
    protected McpSyncClient mcpClient

    def setup() {
        def endpoint = context.environment.getProperty("spring.ai.mcp.server.sse-endpoint")
        def url = "$baseUrl/$endpoint"
        def sseTransport = HttpClientSseClientTransport.builder(url).build()
        mcpClient = McpClient.sync(sseTransport).build()
    }

    def cleanup() {
        if (mcpClient != null) {
            mcpClient.closeGracefully()
        }
    }
}
