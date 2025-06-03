package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest
import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin

class GradleFileScannerTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "can collect gradle files from a build"() {
        when:
        def result = succeeds(GradleNlpUiPlugin.GRADLE_FILES_COLLECTOR_TASK_NAME)

        then:
        result.output.contains("""Collected Gradle build scripts:
settings.gradle
build.gradle""")
    }
}
