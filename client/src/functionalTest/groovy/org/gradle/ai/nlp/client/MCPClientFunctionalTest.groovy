package org.gradle.ai.nlp.client

import org.gradle.ai.nlp.test.TestUtil
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
class MCPClientFunctionalTest extends Specification {
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
        def process = ["java", "-jar", PATH_TO_SERVER_JAR, "--server.port=$port"].execute()
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
        MCPClient client = new MCPClient(baseUrl)

        when:
        client.connect()

        then:
        client.isConnected()
    }

    def "client can query server"() {
        given:
        MCPClient client = new MCPClient(baseUrl)
        client.connect()

        when:
        def result = client.query("What is the capital of France?")

        then:
        result == "42"
    }
}
