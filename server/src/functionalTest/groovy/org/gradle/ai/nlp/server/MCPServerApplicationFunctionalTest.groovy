package org.gradle.ai.nlp.server

import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport
import org.springframework.web.client.RestTemplate

/**
 * Functional tests for the MCP server application.
 * <p>
 * These tests start the server via running it on the test classpath and verifies that
 * it can respond to health checks and SSE connections via a simple client defined in the tests.
 * <p>
 * <strong>Be sure to run the `:server:bootJar` task to generate the server
 * jar prior to running these tests.</strong>
 */
class MCPServerApplicationFunctionalTest extends AbstractMCPServerApplicationFunctionalTest {
    def "can start server and ping server health"() {
        when:
        def restTemplate = new RestTemplate()
        def url = "$baseUrl/actuator/health"
        def response = restTemplate.getForEntity(url, String)

        then:
        response.statusCode.is2xxSuccessful()
        response.body != null
    }

    def "can start server and connect via an SSE client"() {
        given:
        def endpoint = context.environment.getProperty("spring.ai.mcp.server.sse-endpoint")
        def url = "$baseUrl/$endpoint"
        def sseTransport = HttpClientSseClientTransport.builder(url).build()
        def mcpClient = McpClient.sync(sseTransport).build()

        when:
        mcpClient.initialize()

        then:
        mcpClient.initialized

        cleanup:
        mcpClient.closeGracefully()
    }

    // TODO: load example reports from the test resources, start the server with those reports, and then query the server
    // Try using getPrompt() on the mcpClient for this
//    def "ask about flying pigs"() {
//        when:
//        def query = "What task should I run to fly a barnyard animal?  Respond in the following format, substituting X for the task name: To fly an animal, you should run the `X` task."
//        def result = succeeds(AskMCPTask.NAME, "--${AskMCPTask.QUERY_PARAM_NAME}=$query")
//
//        then:
//        result.output.contains(AskMCPTask.QUERY_LOG_MESSAGE_TEMPLATE.replace("{}", query))
//        result.output.contains("To fly an animal, you should run the `flyPig` task.")
//    }
//
//    def "ask about non-default project locations"() {
//        when:
//        def query = "What projects have non-default project locations?  Respond in the following format, substituting X for the project name and Y for the directory: Project X is located in directory Y."
//        def result = succeeds(AskMCPTask.NAME, "--${AskMCPTask.QUERY_PARAM_NAME}=$query")
//
//        then:
//        result.output.contains(AskMCPTask.QUERY_LOG_MESSAGE_TEMPLATE.replace("{}", query))
//        result.output.contains("To fly an animal, you should run the `flyPig` task.")
//    }
}
