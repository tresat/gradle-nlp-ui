package org.gradle.ai.nlp.server

import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport
import org.gradle.ai.nlp.test.TestUtil
import org.gradle.ai.nlp.util.Util
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
/**
 * Functional tests for the MCP server application.
 * <p>
 * These tests start the server via running it on the test classpath and verifies that
 * it can respond to health checks and SSE connections via a simple client defined in the tests.
 * <p>
 * <strong>Be sure to run the `:server:bootJar` task to generate the server
 * jar prior to running these tests.</strong>
 */
class MCPServerApplicationFunctionalTest extends Specification {
    private static String baseUrl
    private static ConfigurableApplicationContext context

    def setupSpec() {
        def port = TestUtil.readPortFromProperties()
        baseUrl = "http://localhost:$port/"

        context = MCPServerApplication.run(
                port,
                Util.readAnthropicApiKeyFromProperties(),
                new File("build/logs/build-mcp-server.log"),
                new File("src/functionalTest/resources/sample-mcp-reports/custom-tasks-report.txt"),
                new File("src/functionalTest/resources/sample-mcp-reports/gradle-tasks-report.txt"),
        )
    }

    def cleanupSpec() {
        if (context && context.isActive()) {
            context.close()
        }
    }

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

    // TODO: move to separate test class per tool - test calls to tools
    // TODO: description is a constant in the tool definition
    def "server makes tasks info tool available"() {
        when:
        def callbacks = context.getBean("tasksInfo").toolCallbacks

        then:
        callbacks.size() == 1
        def tasksInfoTool = callbacks[0]
        tasksInfoTool.toolDefinition.name() == "tasksInfoTool"
        tasksInfoTool.toolDefinition.description() == "Task report information"
    }

    def "server makes gradle files tool available"() {
        when:
        def callbacks = context.getBean("gradleFiles").toolCallbacks

        then:
        callbacks.size() == 1
        def tasksInfoTool = callbacks[0]
        tasksInfoTool.toolDefinition.name() == "gradleFilesTool"
        tasksInfoTool.toolDefinition.description() == "Lists the absolute path to every Gradle file present in the build (and any included builds, recursively), and their contents"

    }

//    def "server makes gradle files contents tool available"() {
//        expect:
//        throw new RuntimeException("Not implemented yet")
//    }
}
