package org.gradle.ai.nlp.plugin

class GradleNlpUiPluginFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "can apply plugin"() {
        when:
        def result = succeeds("tasks")

        then:
        result.output.contains(knownAITasks())
    }
}

