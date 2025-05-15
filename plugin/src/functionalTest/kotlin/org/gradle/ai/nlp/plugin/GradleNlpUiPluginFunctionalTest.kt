package org.gradle.ai.nlp.plugin

import org.gradle.ai.nlp.plugin.service.MCPBuildService
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * A simple functional test for the 'org.gradle.ai.nlp' plugin.
 */
class GradleNlpUiPluginFunctionalTest {
    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle") }

    @Test
    fun `can run task`() {
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id('org.gradle.ai.nlp')
            }
        """.trimIndent())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments(GradleNlpUiPlugin.START_MCP_SERVER_TASK_NAME)
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.output.contains(MCPBuildService.SERVER_START_MESSAGE))
    }
}
