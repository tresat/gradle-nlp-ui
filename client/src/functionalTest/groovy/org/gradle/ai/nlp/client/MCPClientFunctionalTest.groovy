package org.gradle.ai.nlp.client

import org.gradle.ai.nlp.util.Util

/**
 * Functional tests for the MCP client application.
 * <p>
 * These tests start the server via running the jar as an external process and connects to
 * it to exercise the client.
 */
class MCPClientFunctionalTest extends AbstractServerRunningFunctionalTest {
    def "client can connect to server"() {
        given:
        MCPClient client = new MCPClient()

        when:
        client.connect(Util.readAnthropicApiKeyFromProperties(), List.of(serverUrl.toString()))

        then:
        client.isConnected()

        cleanup:
        client.close()
    }

    def "client can query server"() {
        given:
        MCPClient client = new MCPClient()
        client.connect(Util.readAnthropicApiKeyFromProperties(), List.of(serverUrl.toString()))

        when:
        def result = client.query("What task should I run to create a new Gradle project?")

        then:
        result.contains("init")

        cleanup:
        client.close()
    }
}
