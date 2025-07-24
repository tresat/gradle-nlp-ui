package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest

class GradleFilesReportTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "can collect gradle files from a build"() {
        when:
        def result = succeeds(GradleFilesReportTask.TASK_NAME)

        then:
        def output = result.output
        def matcher = output =~ /See the report at: (.*\/${GradleFilesReportTask.REPORTS_FILE_NAME})/
        assert matcher.find()

        and:
        def reportPath = matcher.group(1)
        def reportFile = new File(reportPath)
        assert reportFile.exists()
        println(reportFile.text)

        and:
        def expectedFiles = [
                "build.gradle",
                "settings.gradle"
        ].collect { new File(projectDir, it).absolutePath }
        reportFile.readLines() == expectedFiles
    }
}
