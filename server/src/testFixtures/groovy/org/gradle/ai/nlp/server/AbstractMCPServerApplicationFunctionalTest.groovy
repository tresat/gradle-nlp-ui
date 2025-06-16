package org.gradle.ai.nlp.server

import org.gradle.ai.nlp.test.TestUtil
import org.gradle.ai.nlp.util.Util
import org.springframework.context.ConfigurableApplicationContext
import spock.lang.Specification
import spock.lang.TempDir

class AbstractMCPServerApplicationFunctionalTest extends Specification {
    protected static String baseUrl
    protected static ConfigurableApplicationContext context

    @TempDir
    File tempDir

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
}
