package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest
import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin

class AskMCPTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "query option is required"() {
        when:
        def result = fails(GradleNlpUiPlugin.QUERY_MCP_SERVER_TASK_NAME)

        then:
        result.output.contains("A problem was found with the configuration of task ':${GradleNlpUiPlugin.QUERY_MCP_SERVER_TASK_NAME}' (type '${AskMCPTask.simpleName}').")
        result.output.contains("type '${AskMCPTask.class.name}' property 'query' doesn't have a configured value.")
    }

    def "query can be asked"() {
        when:
        def result = succeeds(GradleNlpUiPlugin.QUERY_MCP_SERVER_TASK_NAME, "--${AskMCPTask.QUERY_PARAM}=Hi?")

        then:
        result.output.contains("Tools available:")

//        result.output.contains("Querying MCP Server: 'Hi?'")
//        result.output.contains("Response from MCP Server: '42'")
    }
}
