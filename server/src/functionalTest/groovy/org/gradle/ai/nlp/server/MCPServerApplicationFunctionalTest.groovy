package org.gradle.ai.nlp.server

/**
 * Functional tests for the MCP server application.
 * <p>
 * These tests start the server via running it on the test classpath and verifies that
 * it can respond to health checks and SSE connections via a simple client defined in the tests.
 * <p>
 * <strong>Be sure to run the `:server:bootJar` task to generate the server
 * jar prior to running these tests.</strong>
 */
class MCPServerApplicationFunctionalTest extends AbstractMCPServerApplicationWithSyncClientFunctionalTest {
    def "can start server and connect via an SSE client"() {
        when:
        mcpClient.initialize()

        then:
        mcpClient.initialized

        expect:
        mcpClient.ping() == [:]
    }
}
