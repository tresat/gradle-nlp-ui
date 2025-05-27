package org.gradle.ai.nlp.plugin.task;

import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin;
import org.gradle.ai.nlp.server.McpServerApplication;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.ai.nlp.plugin.service.MCPServerService;
import org.gradle.work.DisableCachingByDefault;
import org.springframework.boot.SpringApplication;

import java.io.File;

@DisableCachingByDefault
public abstract class StartMCPTask extends DefaultTask {
    @ServiceReference(GradleNlpUiPlugin.MCP_SERVER_SERVICE_NAME)
    abstract Property<MCPServerService> getMCPService();

    @InputFile
    public abstract Property<File> getTasksReportFile();

    public StartMCPTask() {
        getOutputs().upToDateWhen(task -> {
            // This task is never up-to-date
            return false;
        });
    }

    @TaskAction
    public void startServer() {
        new SpringApplication(McpServerApplication.class).run(
                "--spring.config.location=classpath:/application.properties",
                "--org.gradle.ai.nlp.server.tasks.report.file=" + getTasksReportFile().get().getAbsolutePath()
        );
//        MCPServerService serverService = getMCPService().get();
//        serverService.startServer();
    }
}
