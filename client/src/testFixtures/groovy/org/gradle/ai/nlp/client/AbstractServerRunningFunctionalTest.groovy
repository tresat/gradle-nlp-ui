package org.gradle.ai.nlp.client

import org.gradle.ai.nlp.test.TestUtil
import org.gradle.ai.nlp.util.Util
import org.springframework.context.ConfigurableApplicationContext
import spock.lang.Specification

import static java.nio.file.Files.exists
import static java.nio.file.Paths.get

abstract class AbstractServerRunningFunctionalTest extends Specification {
    private static final String PATH_TO_SERVER_JAR = "../server/build/libs/server-0.1.0-SNAPSHOT.jar"

    protected static port = TestUtil.readPortFromProperties()
    protected static serverUrl = new URL("http://localhost:$port/")

    private static serverProcess

    def setupSpec() {
        if (!exists(get(PATH_TO_SERVER_JAR))) {
            System.err.println("Please run the `:server:bootJar` task first to generate the boot JAR.")
            System.exit(1)
        }

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

    protected ConfigurableApplicationContext startClient() {
        String anthropicApiKey = Util.readAnthropicApiKeyFromProperties()
        return SpringMCPClient.run(anthropicApiKey, List.of(serverUrl.toString()))
    }
}
