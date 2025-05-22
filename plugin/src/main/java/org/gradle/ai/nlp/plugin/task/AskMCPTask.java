package org.gradle.ai.nlp.plugin.task;

import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin;
import org.gradle.ai.nlp.plugin.service.MCPClientService;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault
public abstract class AskMCPTask extends DefaultTask {
    public static final String QUERY_PARAM = "query";

    @ServiceReference(GradleNlpUiPlugin.MCP_CLIENT_SERVICE_NAME)
    abstract Property<MCPClientService> getMCPClient();

    @Input
    @Option(option = QUERY_PARAM, description = "Question to ask the MCP Server")
    public abstract Property<String> getQuery();

    public AskMCPTask() {
        getOutputs().upToDateWhen(task -> {
            // This task is never up-to-date
            return false;
        });
    }

    @TaskAction
    public void ask() {
        MCPClientService clientService = getMCPClient().get();
        clientService.setLogger(getLogger());
        clientService.query(getQuery().get());
    }
}
