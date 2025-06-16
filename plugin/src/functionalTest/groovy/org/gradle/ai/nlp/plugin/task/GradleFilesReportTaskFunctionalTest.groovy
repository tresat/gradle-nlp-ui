package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest

class GradleFilesReportTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "can collect gradle files from a build"() {
        when:
        def result = succeeds(GradleFilesReportTask.TASK_NAME)

        then:
        result.output.contains("""Collected Gradle build scripts:
settings.gradle
build.gradle""")
    }
}
