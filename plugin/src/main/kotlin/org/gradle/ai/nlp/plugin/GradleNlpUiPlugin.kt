package org.gradle.ai.nlp.plugin

import org.gradle.ai.nlp.plugin.service.MCPBuildService
import org.gradle.ai.nlp.plugin.task.CustomTasksReport
import org.gradle.ai.nlp.plugin.task.StartMCPTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleNlpUiPlugin: Plugin<Project> {
    companion object {
        const val MCP_SERVICE_NAME = "mcp-server"

        const val START_MCP_SERVER_TASK_NAME = "mcpStartServer"
        const val CUSTOM_TASKS_REPORT_TASK_NAME = "mcpTasksReport"
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun apply(project: Project) {
        project.gradle.sharedServices.registerIfAbsent(MCP_SERVICE_NAME, MCPBuildService::class.java)

        val tasksReport = project.tasks.register(CUSTOM_TASKS_REPORT_TASK_NAME, CustomTasksReport::class.java)

        project.tasks.register(START_MCP_SERVER_TASK_NAME, StartMCPTask::class.java) {
            it.tasksReportFile.set(tasksReport.map { it.outputFile })
            it.inputs.files(tasksReport.get().outputs.files) // As the outputFile of TaskReportTask is not a Provider, we need to establish the task dependency like this
        }
    }
}
