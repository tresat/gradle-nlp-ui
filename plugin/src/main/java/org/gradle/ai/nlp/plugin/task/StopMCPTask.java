package org.gradle.ai.nlp.plugin.task;

import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin;
import org.gradle.ai.nlp.plugin.service.MCPServerService;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault
public abstract class StopMCPTask extends DefaultTask {
    public static final String TASK_NAME = "mcpStopServer";

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
        MCPServerService serverService = getMCPService().get();
        if (!serverService.isStarted()) {
            serverService.close();
        }
    }
}
