package org.gradle.ai.nlp.plugin.task;

import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin;
import org.gradle.ai.nlp.plugin.service.MCPBuildService;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

public abstract class AskMCPTask extends DefaultTask {
    public static final String QUERY_PARAM = "query";

    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server: '{}'";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server: '{}'";

    @ServiceReference(GradleNlpUiPlugin.MCP_SERVICE_NAME)
    abstract Property<MCPBuildService> getMCPService();

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
        MCPBuildService server = getMCPService().get();

        String query = getQuery().get();
        getLogger().lifecycle(QUERYING_MSG_TEMPLATE, query);
        String answer = server.query(getQuery().get());
        getLogger().lifecycle(ANSWER_MSG_TEMPLATE, answer);
    }
}
