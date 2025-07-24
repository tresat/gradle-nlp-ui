package org.gradle.ai.nlp.plugin

import org.apache.commons.io.FileUtils
import org.gradle.ai.nlp.plugin.task.AskMCPTask
import org.gradle.ai.nlp.plugin.task.CustomTasksReportTask
import org.gradle.ai.nlp.plugin.task.ProjectLocationsReportTask
import org.gradle.ai.nlp.plugin.task.StartMCPTask
import org.gradle.ai.nlp.plugin.task.StopMCPTask
import org.gradle.ai.nlp.test.TestUtil
import org.gradle.ai.nlp.util.Util
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

abstract class AbsractGradleNlpUiPluginFunctionalTest extends Specification {
    @TempDir
    protected File projectDir

    protected static int port

    def setupSpec() {
        port = TestUtil.readPortFromProperties()
    }

    def setup() {
        given:
        buildFile.text = """
            plugins {
                id 'org.gradle.ai.nlp'
            }
            
            mcpServer {
                port = $port
                anthropicApiKey = "${Util.readAnthropicApiKeyFromProperties()}"
            }
        """.stripIndent()

        settingsFile.text = """
            rootProject.name = 'test-project'
        """
    }

    protected File getBuildDir() {
        return new File(projectDir, 'build')
    }

    protected File getBuildFile() {
        return new File(projectDir, 'build.gradle')
    }

    protected File getSettingsFile() {
        return new File(projectDir, 'settings.gradle')
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

    protected File file(String path) {
        def file = new File(projectDir, path)
        FileUtils.createParentDirectories(file)
        file.createNewFile()
        return file
    }

    protected String knownAITasks() {
        """AI tasks
--------
${AskMCPTask.NAME} - ${AskMCPTask.DESCRIPTION}
${StartMCPTask.NAME} - ${StartMCPTask.DESCRIPTION}
${StopMCPTask.NAME} - ${StopMCPTask.DESCRIPTION}
${ProjectLocationsReportTask.TASK} - ${ProjectLocationsReportTask.DESCRIPTION}
${CustomTasksReportTask.NAME} - ${CustomTasksReportTask.DESCRIPTION}"""
    }
}
