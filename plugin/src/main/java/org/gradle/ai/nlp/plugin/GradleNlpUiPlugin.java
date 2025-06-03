package org.gradle.ai.nlp.plugin;

import org.gradle.ai.nlp.plugin.service.MCPClientService;
import org.gradle.ai.nlp.plugin.service.MCPServerService;
import org.gradle.ai.nlp.plugin.task.*;
import org.gradle.ai.nlp.util.Util;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class GradleNlpUiPlugin implements Plugin<Project> {
    public static final int MCP_SERVER_DEFAULT_PORT = 8085;
    public static final String MCP_SERVER_LOG_DIR = "log";
    public static final String MCP_SERVER_LOG_FILE_NAME = "mcp-server.log";

    public static final String MCP_SERVER_SERVICE_NAME = "mcp-server";
    public static final String MCP_CLIENT_SERVICE_NAME = "mcp-client";

    public static final String MCP_TASK_GROUP_NAME = "AI";
    public static final String MCP_REPORTS_DIR = "mcp-reports";

    public static final String CUSTOM_TASKS_REPORT_TASK_NAME = "mcpTasksReport";
    public static final String GRADLE_FILES_COLLECTOR_TASK_NAME = "collectGradleFiles";

    public static final String  START_MCP_SERVER_TASK_NAME = "mcpStartServer";
    public static final String  STOP_MCP_SERVER_TASK_NAME = "mcpStopServer";

    public static final String QUERY_MCP_QUERY_TASK_NAME = "ai";

    @Override
    public void apply(Project project) {
        MCPServerExtension extension = project.getExtensions().create("mcpServer", MCPServerExtension.class);
        extension.getPort().convention(MCP_SERVER_DEFAULT_PORT);
        extension.getLogFile().convention(project.getLayout().getBuildDirectory().dir(MCP_SERVER_LOG_DIR).map(d -> d.file(MCP_SERVER_LOG_FILE_NAME)));
        extension.getAnthropicApiKey().convention(project.getProviders().environmentVariable("ANTHROPIC_API_KEY")
                .orElse(project.provider(Util::readAnthropicApiKeyFromProperties)));

        registerServices(project, extension);
        registerTasks(project, extension);
    }

    private void registerTasks(Project project, MCPServerExtension extension) {
        TaskProvider<@NotNull GradleFileScannerTask> gradleFilesCollector = project.getTasks().register(GRADLE_FILES_COLLECTOR_TASK_NAME, GradleFileScannerTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Collects Gradle build scripts from the build and any included builds");

            task.getOutputFile().convention(project.getLayout().getBuildDirectory().dir(MCP_REPORTS_DIR).map(d -> d.file(GradleFileScannerTask.REPORTS_FILE)));
        });

        TaskProvider<@NotNull CustomTasksReportTask> tasksReport = project.getTasks().register(CUSTOM_TASKS_REPORT_TASK_NAME, CustomTasksReportTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Feeds the tasks report output to the MCP server");
        });

        TaskProvider<@NotNull StartMCPTask> startServer = project.getTasks().register(START_MCP_SERVER_TASK_NAME, StartMCPTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Starts the MCP server");

            task.getTasksReportFile().convention(tasksReport.map(CustomTasksReportTask::getOutputFile));
            task.getGradleFilesFile().convention(gradleFilesCollector.map(GradleFileScannerTask::getOutputFile).map(Provider::get));
            task.getLogFile().convention(extension.getLogFile());
        });

        TaskProvider<@NotNull StopMCPTask> stopServer = project.getTasks().register(STOP_MCP_SERVER_TASK_NAME, StopMCPTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Stops the MCP server");
        });

        project.getTasks().register(QUERY_MCP_QUERY_TASK_NAME, AskMCPTask.class, task -> {
            task.setGroup(MCP_TASK_GROUP_NAME);
            task.setDescription("Queries the MCP server");

            task.dependsOn(startServer);
            task.finalizedBy(stopServer);
        });
    }

    private void registerServices(Project project, MCPServerExtension extension) {
        project.getGradle().getSharedServices().registerIfAbsent(MCP_SERVER_SERVICE_NAME, MCPServerService.class, spec -> {
            spec.getParameters().getPort().convention(extension.getPort());
            spec.getParameters().getLogFile().convention(extension.getLogFile());
            spec.getParameters().getTasksReportFile().convention(project.getLayout().getBuildDirectory().dir(MCP_REPORTS_DIR).map(d -> d.file(CustomTasksReportTask.REPORTS_FILE)));
            spec.getParameters().getAnthropicApiKey().convention(extension.getAnthropicApiKey());
        });

        project.getGradle().getSharedServices().registerIfAbsent(MCP_CLIENT_SERVICE_NAME, MCPClientService.class, spec -> {
            spec.getParameters().getAnthropicApiKey().convention(extension.getAnthropicApiKey());
            spec.getParameters().getServerUrls().add(extension.getPort().map(port -> "http://localhost:" + port));
        });
    }
}
