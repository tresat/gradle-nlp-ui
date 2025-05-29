package org.gradle.ai.nlp.client

import org.gradle.ai.nlp.test.TestUtil
import org.gradle.ai.nlp.util.Util
import org.springframework.ai.chat.client.ChatClient
import spock.lang.Specification

import static java.nio.file.Files.exists
import static java.nio.file.Paths.get

/**
 * Functional tests for the MCP client application.
 * <p>
 * These tests start the server via running the jar as an external process and connects to
 * it to exercise the client.
 */
class SpringMCPClientFunctionalTest extends Specification {
    private static final String PATH_TO_SERVER_JAR = "../server/build/libs/server-0.1.0-SNAPSHOT.jar"

    private static String baseUrl
    private static serverProcess

    def setupSpec() {
        if (!exists(get(PATH_TO_SERVER_JAR))) {
            System.err.println("Please run the `:server:bootJar` task first to generate the boot JAR.")
            System.exit(1)
        }
        def port = TestUtil.readPortFromProperties()
        baseUrl = "http://localhost:$port/"

        // Start the server JAR as a background process
        def process = ["java", "-jar", PATH_TO_SERVER_JAR,
            "--server.port=$port",
            "--org.gradle.ai.nlp.server.tasks.report.file=src/functionalTest/resources/sample-mcp-reports/custom-tasks-report.txt",
            "--logging.file.name=build/logs/build-mcp-server.log",
            "--spring.ai.anthropic.api-key=${Util.readAnthropicApiKeyFromProperties()}"
        ].execute()
        process.consumeProcessOutput(System.out, System.err)

        // Store the process for cleanup
        serverProcess = process

        // TODO: find a better way to wait for the server to start
        Thread.sleep(5000)
    }

    def cleanupSpec() {
        if (serverProcess) {
            serverProcess.destroy()
            serverProcess.waitForOrKill(5000)
            serverProcess = null
        }
    }

    def "client can connect to server"() {
        given:
        String anthropicApiKey = Util.readAnthropicApiKeyFromProperties()
        def context = SpringMCPClient.run(anthropicApiKey)

        expect:
        context.isActive()

        cleanup:
        context.close()
    }

    def "client can query server"() {
        given:
        String anthropicApiKey = Util.readAnthropicApiKeyFromProperties()
        def context = SpringMCPClient.run(anthropicApiKey)

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
