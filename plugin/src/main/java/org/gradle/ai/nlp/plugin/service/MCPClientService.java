package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import org.gradle.ai.nlp.client.MCPClient;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

public abstract class MCPClientService implements BuildService<BuildServiceParameters.None>, AutoCloseable {
    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server: '{}'";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server: '{}'";

    private final Logger logger = Logging.getLogger(MCPClientService.class);

    private MCPClient client = new MCPClient();;

    public boolean isConnected() {
        return client != null;
    }

    public void connect() {
        Preconditions.checkState(!isConnected(), "Already connected");
        client.connect();
    }

    public String query(String query) {
        if (!isConnected()) {
            connect();
        }

        logger.lifecycle(QUERYING_MSG_TEMPLATE, query);
        String answer = client.query(query);
        logger.lifecycle(ANSWER_MSG_TEMPLATE, answer);
        return answer;
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
