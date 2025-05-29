package org.gradle.ai.nlp.client;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.ConfigurableApplicationContext;

public class MCPClient implements AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger(MCPClient.class);

    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server: '{}'%n";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server: '{}'%n";

    private ConfigurableApplicationContext clientContext;

    public void connect() {
        Preconditions.checkState(!isConnected(), "Client is already connected");
        clientContext = SpringMCPClient.run();
    }

    public boolean isConnected() {
        return clientContext != null && clientContext.isActive();
    }

    public String query(String query) {
        Preconditions.checkState(isConnected(), "Client not connected");

        logger.info(QUERYING_MSG_TEMPLATE, query);

        var chatClient = clientContext.getBean(ChatClient.class);
        var answer = chatClient.prompt(query).call().content();
        logger.info(ANSWER_MSG_TEMPLATE, answer);

        return answer;
    }

    @Override
    public void close() {
        if (isConnected()) {
            clientContext.close();
            logger.info("Client closed gracefully.");
        }
    }
}
