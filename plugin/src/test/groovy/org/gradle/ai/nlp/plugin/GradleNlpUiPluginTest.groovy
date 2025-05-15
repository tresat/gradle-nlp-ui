package org.gradle.ai.nlp.plugin

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class GradleNlpUiPluginTest extends Specification {
    @SuppressWarnings('ConfigurationAvoidance')
    def "plugin registers task"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply("org.gradle.ai.nlp")

        expect:
        project.gradle.getSharedServices().getRegistrations().findByName(GradleNlpUiPlugin.MCP_SERVICE_NAME)
        project.tasks.named(GradleNlpUiPlugin.CUSTOM_TASKS_REPORT_TASK_NAME).get()
        project.tasks.named(GradleNlpUiPlugin.START_MCP_SERVER_TASK_NAME).get()
    }
}
