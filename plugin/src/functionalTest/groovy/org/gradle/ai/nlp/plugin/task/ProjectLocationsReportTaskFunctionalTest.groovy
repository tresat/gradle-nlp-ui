package org.gradle.ai.nlp.plugin.task


import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest

class ProjectLocationsReportTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "can run projects report task #ccStatus"() {
        when:
        def result = succeeds(ProjectLocationsReportTask.TASK, ccStatus)

        then:
        def output = result.output
        def matcher = output =~ /See the report at: (.*\/${ProjectLocationsReportTask.REPORTS_FILE_NAME})/
        assert matcher.find()

        and:
        def reportPath = matcher.group(1)
        def reportFile = new File(reportPath)
        assert reportFile.exists()
        println(reportFile.text)

        and:
        reportFile.text.contains("Gradle Project Locations Report")
        reportFile.text.contains("Root Project: test-project")

        where:
        ccStatus << ["--configuration-cache", "--no-configuration-cache"]
    }

    def "can run projects report task with multiproject build"() {
        given:
        settingsFile.append("""
            include 'core', 'ui'
        """)
        file("core/build.gradle") << """
            plugins {
                id 'java-library'
            }
        """
        file("ui/build.gradle") << """
            plugins {
                id 'java-library'
            }
        """

        when:
        def result = succeeds(ProjectLocationsReportTask.TASK)

        then:
        def output = result.output
        def matcher = output =~ /See the report at: (.*\/${ProjectLocationsReportTask.REPORTS_FILE_NAME})/
        assert matcher.find()

        and:
        def reportPath = matcher.group(1)
        def reportFile = new File(reportPath)
        assert reportFile.exists()
        println(reportFile.text)

        and:
        reportFile.text.contains("Gradle Project Locations Report")
        reportFile.text.contains("Root Project: test-project")
        reportFile.text.contains("Project: core")
        reportFile.text.contains("Project: ui")
    }
}
