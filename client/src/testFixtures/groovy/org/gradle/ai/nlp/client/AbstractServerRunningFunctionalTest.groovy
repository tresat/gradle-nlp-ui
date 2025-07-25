package org.gradle.ai.nlp.client

import org.gradle.ai.nlp.test.TestUtil
import org.gradle.ai.nlp.util.ServerKeys
import org.gradle.ai.nlp.util.Util
import org.springframework.context.ConfigurableApplicationContext
import spock.lang.Specification

import static java.nio.file.Files.exists
import static java.nio.file.Paths.get

abstract class AbstractServerRunningFunctionalTest extends Specification implements ServerKeys {
    private static final String PATH_TO_SERVER_JAR = "../server/build/libs/server-0.1.0-SNAPSHOT.jar"

    protected static port = TestUtil.readPortFromProperties()
    protected static serverUrl = new URL("http://localhost:$port/")

    private static Process serverProcess

    protected static outputStream = new ByteArrayOutputStream()
    protected static errorStream = new ByteArrayOutputStream()

    def setupSpec() {
        if (!exists(get(PATH_TO_SERVER_JAR))) {
            System.err.println("Please run the `:server:bootJar` task first to generate the boot JAR.")
            System.exit(1)
        }

        if (isServerRunning()) {
            System.err.println("Server is already running at $serverUrl. Please stop it before running these tests.")
            System.exit(1)
        }

        // Start the server JAR as a background process
        def process = ["java", "-jar", PATH_TO_SERVER_JAR,
                       "--${SERVER_PORT_PROPERTY}=$port",
                       "--${PROJECT_LOCATIONS_REPORT_FILE_PROPERTY}=src/functionalTest/resources/sample-mcp-reports/project-locations-report.txt",
                       "--${TASKS_REPORT_FILE_PROPERTY}=src/functionalTest/resources/sample-mcp-reports/custom-tasks-report.txt",
                       "--${LOG_FILE_PROPERTY}=build/logs/build-mcp-server.log",
                       "--${ANTHROPIC_API_KEY_PROPERTY}=${Util.readAnthropicApiKeyFromProperties()}"
        ].execute()
        process.consumeProcessOutput(new PrintStream(outputStream), new PrintStream(errorStream))

        // Store the process for cleanup
        serverProcess = process

        // TODO: find a better way to wait for the server to start
        Thread.sleep(2000)
    }

    def cleanupSpec() {
        if (serverProcess) {
            serverProcess.destroy()
            serverProcess.waitForOrKill(2000)
            serverProcess = null
        }
    }

    // If this test fails, it probably means the server didn't stop correctly on the last run and needs to be killed manually
    // Check http://localhost:8082/sse (serverUrl)
    def "can start server for functional client tests"() {
        given:
        // TODO: find a better way to wait for the server to start
        Thread.sleep(2000)

        expect:
        serverProcess.alive
        def output = outputStream.toString()
        output.contains("Started MCPServerApplication")
        output.contains(LISTENING_ON + serverUrl)
    }

    protected ConfigurableApplicationContext startClient() {
        String anthropicApiKey = Util.readAnthropicApiKeyFromProperties()
        return SpringMCPClient.run(anthropicApiKey, List.of(serverUrl.toString()))
    }

    private boolean isServerRunning() {
        try {
            def conn = serverUrl.openConnection()
            conn.setConnectTimeout(1000)
            conn.setReadTimeout(1000)
            conn.inputStream.close()
            return true
        } catch (Exception e) {
            return false
        }
    }
}
