package org.gradle.ai.nlp.plugin

import org.gradle.ai.nlp.plugin.task.CustomTasksReportTask
import org.gradle.ai.nlp.plugin.task.GradleFilesReportTask
import org.gradle.ai.nlp.plugin.task.StartMCPTask
import org.gradle.ai.nlp.plugin.task.StopMCPTask
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

        Task tasksReportTask = project.tasks.named(CustomTasksReportTask.TASK_NAME).get()
        tasksReportTask.group == GradleNlpUiPlugin.MCP_TASK_GROUP_NAME

        Task gradleFilesReportTask = project.tasks.named(GradleFilesReportTask.TASK_NAME).get()
        gradleFilesReportTask.group == GradleNlpUiPlugin.MCP_TASK_GROUP_NAME

        Task startServerTask = project.tasks.named(StartMCPTask.TASK_NAME).get()
        startServerTask.group == GradleNlpUiPlugin.MCP_TASK_GROUP_NAME

        Task stopServerTask = project.tasks.named(StopMCPTask.TASK_NAME).get()
        stopServerTask.group == GradleNlpUiPlugin.MCP_TASK_GROUP_NAME

        Task queryServerTask = project.tasks.named(StopMCPTask.TASK_NAME).get()
        queryServerTask.group == GradleNlpUiPlugin.MCP_TASK_GROUP_NAME
    }
}
