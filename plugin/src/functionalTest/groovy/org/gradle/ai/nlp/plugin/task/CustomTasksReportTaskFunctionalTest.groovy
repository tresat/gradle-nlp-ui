package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest
import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin

class CustomTasksReportTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "can run tasks report task"() {
        when:
        def result = succeeds(GradleNlpUiPlugin.CUSTOM_TASKS_REPORT_TASK_NAME)

        then:
        String pathToReportInProject = "${CustomTasksReportTask.MCP_REPORTS_DIR}/${CustomTasksReportTask.REPORTS_FILE}"
        def regex = /See the report at: file:\/\/.*$pathToReportInProject/
        result.output.find(regex)

        and:
        def reportFile = new File(buildDir, pathToReportInProject)
        reportFile.exists()

        and:
        reportFile.text.contains(knownAITasks())
    }
}
