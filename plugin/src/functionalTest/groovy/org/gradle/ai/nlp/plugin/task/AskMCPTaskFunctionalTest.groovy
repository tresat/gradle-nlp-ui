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

    // TODO:NEXT Do an actual query that requirest the MCP server to be running to answer it
    def "query can be asked"() {
        when:
        def query = "Hi?"
        def result = succeeds(GradleNlpUiPlugin.QUERY_MCP_QUERY_TASK_NAME, "--${AskMCPTask.QUERY_PARAM_NAME}=$query")

        then:
        result.output.contains(AskMCPTask.QUERY_LOG_MESSAGE_TEMPLATE.replace("{}", query))
        !result.output.isEmpty()
    }
}
