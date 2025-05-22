package org.gradle.ai.nlp.plugin;

import org.gradle.ai.nlp.plugin.service.MCPBuildService;
import org.gradle.ai.nlp.plugin.task.AskMCPTask;
import org.gradle.ai.nlp.plugin.task.CustomTasksReportTask;
import org.gradle.ai.nlp.plugin.task.StartMCPTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public abstract class GradleNlpUiPlugin implements Plugin<Project> {
    public static final String MCP_SERVICE_NAME = "mcp-server";

    public static final String MCP_TASK_GROUP_NAME = "AI";

    public static final String  START_MCP_SERVER_TASK_NAME = "mcpStartServer";
    public static final String CUSTOM_TASKS_REPORT_TASK_NAME = "mcpTasksReport";

    public static final String QUERY_MCP_SERVER_TASK_NAME = "ai";

    @Override
    public void apply(Project project) {
        project.getGradle().getSharedServices().registerIfAbsent(MCP_SERVICE_NAME, MCPBuildService.class);

        TaskProvider<CustomTasksReportTask> tasksReport = project.getTasks().register(CUSTOM_TASKS_REPORT_TASK_NAME, CustomTasksReportTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Feeds the tasks report output to the MCP server");
        });

        TaskProvider<StartMCPTask> startServer = project.getTasks().register(START_MCP_SERVER_TASK_NAME, StartMCPTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Starts the MCP server");
            task.getTasksReportFile().set(tasksReport.map(CustomTasksReportTask::getOutputFile));
            task.getInputs().files(tasksReport.get().getOutputs().getFiles());
        });

        project.getTasks().register(QUERY_MCP_SERVER_TASK_NAME, AskMCPTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Queries the MCP server");
            task.dependsOn(startServer);
        });
    }
}
