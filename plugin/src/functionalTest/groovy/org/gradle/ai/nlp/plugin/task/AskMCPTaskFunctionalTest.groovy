package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest
import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin

class AskMCPTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "query option is required"() {
        when:
        def result = fails(GradleNlpUiPlugin.QUERY_MCP_QUERY_TASK_NAME)

        then:
        result.output.contains("A problem was found with the configuration of task ':${GradleNlpUiPlugin.QUERY_MCP_QUERY_TASK_NAME}' (type '${AskMCPTask.simpleName}').")
        result.output.contains("type '${AskMCPTask.class.name}' property 'query' doesn't have a configured value.")
    }

    def "general Gradle query can be asked"() {
        when:
        def query = "What task should I run to create a new Gradle project?  Respond in the following format, substituting X for the task name: To create a new Gradle project, you should run the `X` task."
        def result = succeeds(GradleNlpUiPlugin.QUERY_MCP_QUERY_TASK_NAME, "--${AskMCPTask.QUERY_PARAM_NAME}=$query")

        then:
        result.output.contains(AskMCPTask.QUERY_LOG_MESSAGE_TEMPLATE.replace("{}", query))
        result.output.contains("To create a new Gradle project, you should run the `init` task.")
    }
}
