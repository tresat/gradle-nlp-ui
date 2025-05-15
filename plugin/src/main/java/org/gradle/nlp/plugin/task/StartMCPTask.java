package org.gradle.nlp.plugin.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.nlp.plugin.service.MCPBuildService;

import java.io.File;

public abstract class StartMCPTask extends DefaultTask {
    // This property provides access to the service instance
    @ServiceReference("mcp")
    abstract Property<MCPBuildService> getMCPService();

    @InputFile
    public abstract Property<File> getTasksReportFile();

    public StartMCPTask() {
        getOutputs().upToDateWhen(task -> {
            // This task is never up-to-date
            return false;
        });
    }

    @TaskAction
    public void download() {
        MCPBuildService server = getMCPService().get();
        server.useServer();
    }
}
