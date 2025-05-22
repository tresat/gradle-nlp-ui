package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import org.gradle.api.logging.Logger;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

public abstract class MCPClientService implements BuildService<BuildServiceParameters.None> {
    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server: '{}'";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server: '{}'";

    private Logger logger;

    public void setLogger(Logger logger) {
        Preconditions.checkState(this.logger == null, "Logger already set");
        this.logger = logger;
    }

    public String query(String query) {
        Preconditions.checkState(logger != null, "Logger not set");

        logger.lifecycle(QUERYING_MSG_TEMPLATE, query);
        String answer = "42"; // Simulated response from the MCP server
        logger.lifecycle(ANSWER_MSG_TEMPLATE, answer);
        return answer;
    }
}
