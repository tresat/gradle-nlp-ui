package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest
import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin

class CustomTasksReportTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "can run tasks report task #ccStatus"() {
        when:
        def result = succeeds(CustomTasksReportTask.NAME, ccStatus)

        then:
        String pathToReportInProject = "${GradleNlpUiPlugin.MCP_REPORTS_DIR}${File.separatorChar}${CustomTasksReportTask.REPORTS_FILE_NAME}"
        def regex = /See the report at: file:\/\/.*$pathToReportInProject/
        result.output.find(regex)

        and:
        def reportFile = new File(buildDir, pathToReportInProject)
        reportFile.exists()
        println(reportFile.text)

        and:
        reportFile.text.contains(knownAITasks())

        where:
        ccStatus << ["--configuration-cache", "--no-configuration-cache"]
    }
}
