package org.gradle.ai.nlp.client

import org.gradle.ai.nlp.test.TestUtil
import org.springframework.ai.chat.client.ChatClient
import spock.lang.Requires

/**
 * Functional tests for the MCP client application.
 * <p>
 * These tests start the server via running the jar as an external process and connects to
 * it to exercise the client.
 */
class SpringMCPClientFunctionalTest extends AbstractServerRunningFunctionalTest {
    def "client can connect to server"() {
        given:
        def context = startClient()

        expect:
        context.isActive()

        cleanup:
        context.close()
    }

    @Requires({ TestUtil.isAnthropicAvailable() })
    def "client can query server"() {
        given:
        def context = startClient()

        when:
        ChatClient chatClient = context.getBean(ChatClient.class)
        var response = chatClient.prompt("List all the tasks available in this build").call().content()
        System.out.println("Response: " + response)

        then:
        response != null

        // Verify that the response contains some of the expected tasks
        response.contains("ai - Queries the MCP server")
        response.contains("mcpStartServer - Starts the MCP server")
        response.contains("init - Initializes a new Gradle build")
        response.contains("wrapper - Generates Gradle wrapper files")

        cleanup:
        context.close()
    }
}
