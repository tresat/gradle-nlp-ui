package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest
import org.gradle.ai.nlp.plugin.service.MCPServerService
import org.gradle.ai.nlp.util.Util
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Requires

class StartMCPTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "can run start server task"() {
        when:
        def result = succeeds(StartMCPTask.NAME, "-S")

        then:
        result.output.contains(MCPServerService.SERVER_STARTUP_MESSAGE)
        result.output.contains(MCPServerService.SERVER_SHUTDOWN_MESSAGE)
    }

    static class WithoutAnthropicApiKeySetInBuild extends AbsractGradleNlpUiPluginFunctionalTest {
        def "can run start server task using ANTHROPIC_API_KEY from environment"() {
            given: "Set up the build file without an API key"
            buildFile.text = """
                plugins {
                    id 'org.gradle.ai.nlp'
                }
                
                mcpServer {
                    port = $port
                }
            """.stripIndent()

            when: "Providing the ANTHROPIC_API_KEY as an environment variable"
            def result = GradleRunner.create()
                    .withProjectDir(projectDir)
                    .withArguments(StartMCPTask.NAME)
                    .withEnvironment(["ANTHROPIC_API_KEY": Util.readAnthropicApiKeyFromProperties()])
                    .withPluginClasspath()
                    .forwardOutput()
                    .build()

            then:
            result.output.contains(MCPServerService.SERVER_STARTUP_MESSAGE)
            result.output.contains(MCPServerService.SERVER_SHUTDOWN_MESSAGE)
        }

        @Requires({ System.getenv("ANTHROPIC_API_KEY") == null })
        def "service gives good error message if Anthropic API key not set"() {
            when: "Set up the build file without an API key"
            buildFile.text = """
                plugins {
                    id 'org.gradle.ai.nlp'
                }
                
                mcpServer {
                    port = $port
                }
            """.stripIndent()

            then:
            def result = fails(StartMCPTask.NAME)

            and:
            result.output.contains("Anthropic API key is not available in the service parameters - was it set in the plugin extension or via the ANTHROPIC_API_KEY environment variable?")
        }

        def setup() {
            buildFile.text = """
                plugins {
                    id 'org.gradle.ai.nlp'
                }
                
                mcpServer {
                    port = $port
                }
            """.stripIndent()
        }
    }
}
