package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest
import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin
import org.gradle.api.tasks.diagnostics.BuildEnvironmentReportTask
import spock.lang.PendingFeature

class CustomBuildEnvironmentReportTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    // TODO: passes now because of notCompatibleWithConfigurationCache on test; otherwise need @PendingFeature(reason = "Configuration cache is broken with overriding @Injected methods in tasks")
    def "can run buildEnvironment report task #ccStatus"() {
        when:
        def result = succeeds(CustomBuildEnvironmentReportTask.NAME, ccStatus)

        then:
        String pathToReportInProject = "${GradleNlpUiPlugin.MCP_REPORTS_DIR}${File.separatorChar}${CustomBuildEnvironmentReportTask.REPORTS_FILE_NAME}"
        def regex = /See the report at: .*$pathToReportInProject/
                result.output.find(regex)

        and:
        def reportFile = new File(buildDir, pathToReportInProject)
        reportFile.exists()

        and:
        reportFile.text.size() == buildEnvironmentReportOutput.size()
        reportFile.text == buildEnvironmentReportOutput

        where:
        ccStatus << ["--configuration-cache", "--no-configuration-cache"]
    }

    private String getBuildEnvironmentReportOutput() {
        def result = succeeds(BuildEnvironmentReportTask.TASK_NAME)
        int startIdx = result.output.indexOf("Daemon JVM")
        int endIdx = result.output.indexOf("BUILD SUCCESSFUL")
        return result.output.substring(startIdx, endIdx - 1)
    }
}
