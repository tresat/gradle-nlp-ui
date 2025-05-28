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
public abstract class StopMCPTask extends DefaultTask {
    @ServiceReference(GradleNlpUiPlugin.MCP_SERVER_SERVICE_NAME)
    abstract Property<MCPServerService> getMCPService();

    public StopMCPTask() {
        getOutputs().upToDateWhen(task -> {
            // This task is never up-to-date
            return false;
        });
    }

    @TaskAction
    public void stopServer() {
        getLogger().lifecycle("Stopping MCP server in task...");
        MCPServerService serverService = getMCPService().get();
        serverService.close();
        getLogger().lifecycle("Stopped MCP server in task.");
    }
}
