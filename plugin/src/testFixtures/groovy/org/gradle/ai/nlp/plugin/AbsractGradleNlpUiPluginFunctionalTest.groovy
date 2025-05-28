package org.gradle.ai.nlp.plugin

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir


class AbsractGradleNlpUiPluginFunctionalTest extends Specification {
    @TempDir
    protected File projectDir

    protected File getBuildDir() {
        return new File(projectDir, 'build')
    }

    protected File getBuildFile() {
        return new File(projectDir, 'build.gradle')
    }

    protected File getSettingsFile() {
        return new File(projectDir, 'settings.gradle')
    }

    def setup() {
        given:
        buildFile.text = """
            plugins {
                id 'org.gradle.ai.nlp'
            }
            
            mcpServer {
                logFile = project.layout.buildDirectory.dir("log").map { d -> d.file("mcp-server.log") }
            }
        """.stripIndent()

        settingsFile.text = """
            rootProject.name = 'test-project'
        """
    }

    protected BuildResult succeeds(String... args) {
        def runner = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(args)
                .withPluginClasspath()
                .forwardOutput()
        return runner.build()
    }

    protected BuildResult fails(String... args) {
        def runner = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(args)
                .withPluginClasspath()
                .forwardOutput()
        return runner.buildAndFail()
    }

    protected String knownAITasks() {
        """AI tasks
--------
ai - Queries the MCP server
mcpStartServer - Starts the MCP server
mcpStopServer - Stops the MCP server
mcpTasksReport - Feeds the tasks report output to the MCP server"""
    }
}
