package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest

class AskMCPTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "query option is required"() {
        when:
        def result = fails(AskMCPTask.NAME)

        then:
        result.output.contains("A problem was found with the configuration of task ':${AskMCPTask.NAME}' (type '${AskMCPTask.simpleName}').")
        result.output.contains("type '${AskMCPTask.class.name}' property 'query' doesn't have a configured value.")
    }
}
