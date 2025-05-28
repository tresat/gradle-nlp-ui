package org.gradle.ai.nlp.plugin;

import org.gradle.ai.nlp.plugin.service.MCPClientService;
import org.gradle.ai.nlp.plugin.service.MCPServerService;
import org.gradle.ai.nlp.plugin.task.AskMCPTask;
import org.gradle.ai.nlp.plugin.task.CustomTasksReportTask;
import org.gradle.ai.nlp.plugin.task.StartMCPTask;
import org.gradle.ai.nlp.plugin.task.StopMCPTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public abstract class GradleNlpUiPlugin implements Plugin<Project> {
    public static final String MCP_SERVER_LOG_DIR = "log";
    public static final String MCP_SERVER_LOG_FILE_NAME = "mcp-server.log";

    public static final String MCP_SERVER_SERVICE_NAME = "mcp-server";
    public static final String MCP_CLIENT_SERVICE_NAME = "mcp-client";

    public static final String MCP_TASK_GROUP_NAME = "AI";

    public static final String CUSTOM_TASKS_REPORT_TASK_NAME = "mcpTasksReport";

    public static final String  START_MCP_SERVER_TASK_NAME = "mcpStartServer";
    public static final String  STOP_MCP_SERVER_TASK_NAME = "mcpStopServer";

    public static final String QUERY_MCP_SERVER_TASK_NAME = "ai";

    @Override
    public void apply(Project project) {
        project.getGradle().getSharedServices().registerIfAbsent(MCP_SERVER_SERVICE_NAME, MCPServerService.class);
        project.getGradle().getSharedServices().registerIfAbsent(MCP_CLIENT_SERVICE_NAME, MCPClientService.class);

        MCPServerExtension extension = project.getExtensions().create("mcpServer", MCPServerExtension.class);
        extension.getLogFile().convention(project.getLayout().getBuildDirectory().dir(MCP_SERVER_LOG_DIR).map(d -> d.file(MCP_SERVER_LOG_FILE_NAME)));
        extension.getTasksReportFile().convention(project.getLayout().getBuildDirectory().dir(CustomTasksReportTask.MCP_REPORTS_DIR).map(d -> d.file(CustomTasksReportTask.REPORTS_FILE)));

        TaskProvider<CustomTasksReportTask> tasksReport = project.getTasks().register(CUSTOM_TASKS_REPORT_TASK_NAME, CustomTasksReportTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Feeds the tasks report output to the MCP server");
        });

        TaskProvider<StartMCPTask> startServer = project.getTasks().register(START_MCP_SERVER_TASK_NAME, StartMCPTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Starts the MCP server");
            task.getTasksReportFile().set(tasksReport.map(CustomTasksReportTask::getOutputFile));
            task.getInputs().files(tasksReport.get().getOutputs().getFiles());

            task.getLogFile().convention(extension.getLogFile());
            task.getTasksReportFile().convention(extension.getTasksReportFile().getAsFile());
        });

        TaskProvider<StopMCPTask> stopServer = project.getTasks().register(STOP_MCP_SERVER_TASK_NAME, StopMCPTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Stops the MCP server");
        });

        project.getTasks().register(QUERY_MCP_SERVER_TASK_NAME, AskMCPTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Queries the MCP server");
            task.dependsOn(startServer);

            task.finalizedBy(stopServer);
        });
    }
}
