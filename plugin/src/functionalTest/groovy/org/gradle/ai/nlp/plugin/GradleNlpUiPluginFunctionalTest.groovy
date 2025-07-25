package org.gradle.ai.nlp.plugin

import org.gradle.ai.nlp.plugin.task.AskMCPTask
import spock.lang.Requires
import org.gradle.ai.nlp.test.TestUtil

class GradleNlpUiPluginFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "can apply plugin"() {
        when:
        def result = succeeds("tasks")

        then:
        result.output.contains(knownAITasks())
    }

    @Requires({ TestUtil.isAnthropicAvailable() })
    def "general Gradle query can be asked"() {
        when:
        def query = "What task should I run to create a new Gradle project?  Respond in the following format, substituting X for the task name: To create a new Gradle project, you should run the `X` task."
        def result = succeeds(AskMCPTask.NAME, "--${AskMCPTask.QUERY_PARAM_NAME}=$query")

        then:
        result.output.contains(AskMCPTask.QUERY_LOG_MESSAGE_TEMPLATE.replace("{}", query))
        result.output.contains("To create a new Gradle project, you should run the `init` task.")
    }
}
