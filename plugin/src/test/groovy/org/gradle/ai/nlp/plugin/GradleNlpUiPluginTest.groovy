package org.gradle.ai.nlp.plugin

import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class GradleNlpUiPluginTest extends Specification {
    def "plugin registers task"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply("org.gradle.ai.nlp")

        expect:
        project.gradle.getSharedServices().getRegistrations().named(GradleNlpUiPlugin.MCP_SERVER_SERVICE_NAME)

        Task tasksReportTask = project.tasks.named(GradleNlpUiPlugin.CUSTOM_TASKS_REPORT_TASK_NAME).get()
        tasksReportTask.group == GradleNlpUiPlugin.MCP_TASK_GROUP_NAME

        Task startServerTask = project.tasks.named(GradleNlpUiPlugin.START_MCP_SERVER_TASK_NAME).get()
        startServerTask.group == GradleNlpUiPlugin.MCP_TASK_GROUP_NAME

        Task queryServerTask = project.tasks.named(GradleNlpUiPlugin.QUERY_MCP_SERVER_TASK_NAME).get()
        queryServerTask.group == GradleNlpUiPlugin.MCP_TASK_GROUP_NAME
    }
}
