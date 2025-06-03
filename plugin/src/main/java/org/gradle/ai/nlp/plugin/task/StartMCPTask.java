package org.gradle.ai.nlp.plugin.task;

import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.ai.nlp.plugin.service.MCPServerService;
import org.gradle.work.DisableCachingByDefault;

import java.io.File;

@DisableCachingByDefault
public abstract class StartMCPTask extends DefaultTask {
    public static final String TASK_NAME = "mcpStartServer";

    @ServiceReference(GradleNlpUiPlugin.MCP_SERVER_SERVICE_NAME)
    abstract Property<MCPServerService> getMCPService();

    @InputFile
    public abstract Property<File> getTasksReportFile();

    @InputFile
    public abstract RegularFileProperty getGradleFilesFile();

    public StartMCPTask() {
        getOutputs().upToDateWhen(task -> {
            // This task is never up-to-date
            return false;
        });
    }

    @TaskAction
    public void startServer() {


        MCPServerService serverService = getMCPService().get();
        serverService.startServer();
    }
}
