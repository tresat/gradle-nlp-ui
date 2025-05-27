package org.gradle.ai.nlp.server

import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class McpServerApplicationFunctionalTest extends Specification {
    private static final String BASE_URL = "http://localhost:8080/" // TODO: make port configurable

    private static ConfigurableApplicationContext context

    def setupSpec() {
        context = McpServerApplication.run()
    }

    def cleanupSpec() {
        if (context != null && context.isActive()) {
            context.close()
        }
    }

    def "can start server and ping server health"() {
        when:
        def restTemplate = new RestTemplate()
        def url = "$BASE_URL/actuator/health"
        def response = restTemplate.getForEntity(url, String)

        then:
        response.statusCode.is2xxSuccessful()
        response.body != null
    }

    def "can start server and connect via SSE client"() {
        given:
        def url = "$BASE_URL/sse" // TODO: grab SSE from props
        def sseTransport = HttpClientSseClientTransport.builder(url).build()
        def mcpClient = McpClient.sync(sseTransport).build()

        when:
        mcpClient.initialize()

        then:
        mcpClient.initialized

        Thread.sleep(10000000)
    }
}
