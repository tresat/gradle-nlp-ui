package org.gradle.ai.nlp.plugin;

import org.gradle.ai.nlp.plugin.service.MCPClientService;
import org.gradle.ai.nlp.plugin.service.MCPServerService;
import org.gradle.ai.nlp.plugin.task.*;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;

public abstract class GradleNlpUiPlugin implements Plugin<Project> {
    public static final int MCP_SERVER_DEFAULT_PORT = 8085;
    public static final String MCP_SERVER_LOG_DIR = "log";
    public static final String MCP_SERVER_LOG_FILE_NAME = "mcp-server.log";

    public static final String MCP_SERVER_SERVICE_NAME = "mcp-server";
    public static final String MCP_CLIENT_SERVICE_NAME = "mcp-client";

    public static final String MCP_TASK_GROUP_NAME = "AI";
    public static final String MCP_REPORTS_DIR = "mcp-reports";

    @Override
    public void apply(Project project) {
        MCPServerExtension extension = project.getExtensions().create("mcpServer", MCPServerExtension.class);
        extension.getPort().convention(MCP_SERVER_DEFAULT_PORT);
        extension.getLogFile().convention(project.getLayout().getBuildDirectory().dir(MCP_SERVER_LOG_DIR).map(d -> d.file(MCP_SERVER_LOG_FILE_NAME)));
        extension.getAnthropicApiKey().convention(project.getProviders().environmentVariable("ANTHROPIC_API_KEY"));

        registerTasks(project);
        registerServices(project, extension);
    }

    private void registerTasks(Project project) {
        TaskProvider<ProjectLocationsReportTask> projectLocationsReport = project.getTasks().register(ProjectLocationsReportTask.TASK, ProjectLocationsReportTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription(ProjectLocationsReportTask.DESCRIPTION);

            task.getOutputFile().convention(project.getLayout().getBuildDirectory().dir(MCP_REPORTS_DIR).map(d -> d.file(ProjectLocationsReportTask.REPORTS_FILE_NAME)));
        });

        TaskProvider<CustomTasksReportTask> tasksReport = project.getTasks().register(CustomTasksReportTask.NAME, CustomTasksReportTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription(CustomTasksReportTask.DESCRIPTION);
        });

        TaskProvider<StartMCPTask> startServer = project.getTasks().register(StartMCPTask.NAME, StartMCPTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription(StartMCPTask.DESCRIPTION);

            task.getTasksReportFile().convention(tasksReport.map(CustomTasksReportTask::getOutputFile));
            task.getProjectLocationsReportFile().convention(projectLocationsReport.map(ProjectLocationsReportTask::getOutputFile).map(Provider::get));
        });

        TaskProvider<StopMCPTask> stopServer = project.getTasks().register(StopMCPTask.NAME, StopMCPTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription(StopMCPTask.DESCRIPTION);
        });

        project.getTasks().register(AskMCPTask.NAME, AskMCPTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription(AskMCPTask.DESCRIPTION);

            task.dependsOn(startServer);
            task.finalizedBy(stopServer);
        });
    }

    private void registerServices(Project project, MCPServerExtension extension) {
        project.getGradle().getSharedServices().registerIfAbsent(MCP_SERVER_SERVICE_NAME, MCPServerService.class, spec -> {
            spec.getParameters().getPort().convention(extension.getPort());
            spec.getParameters().getAnthropicApiKey().convention(extension.getAnthropicApiKey());
            spec.getParameters().getLogFile().convention(extension.getLogFile());

            spec.getParameters().getTasksReportFile().convention(project.getTasks().named(CustomTasksReportTask.NAME, CustomTasksReportTask.class).map(CustomTasksReportTask::getRegularOutputFile).map(Provider::get));
            spec.getParameters().getProjectLocationsReportFile().convention(project.getTasks().named(ProjectLocationsReportTask.TASK, ProjectLocationsReportTask.class).map(ProjectLocationsReportTask::getOutputFile).map(Provider::get));
        });

        project.getGradle().getSharedServices().registerIfAbsent(MCP_CLIENT_SERVICE_NAME, MCPClientService.class, spec -> {
            spec.getParameters().getAnthropicApiKey().convention(extension.getAnthropicApiKey());
            spec.getParameters().getServerUrls().add(extension.getPort().map(port -> "http://localhost:" + port));
        });
    }
}
