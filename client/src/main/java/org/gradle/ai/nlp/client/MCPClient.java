package org.gradle.ai.nlp.client;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Collections;
import java.util.List;

public class MCPClient implements AutoCloseable {
    private final Logger logger;

    public static final String QUERYING_MSG_TEMPLATE = "Querying MCP Server: '{}'%n";
    public static final String ANSWER_MSG_TEMPLATE = "Response from MCP Server: '{}'%n";

    private AnnotationConfigApplicationContext clientContext;

    public MCPClient() {
        this(LoggerFactory.getLogger(MCPClient.class));
    }

    public MCPClient(Logger logger) {
        this.logger = logger;
    }

    public void connect(String anthropicApiKey) {
        connect(anthropicApiKey, Collections.emptyList());
    }

    public void connect(String anthropicApiKey, List<String> mcpServerUrls) {
        Preconditions.checkState(!isConnected(), "Client is already connected");
        clientContext = (AnnotationConfigApplicationContext) SpringMCPClient.run(anthropicApiKey, mcpServerUrls);
        clientContext.registerShutdownHook();
    }

    public boolean isConnected() {
        return clientContext != null && clientContext.isActive();
    }

    public String query(String query) {
        Preconditions.checkState(isConnected(), "Client not connected");

        logger.info(QUERYING_MSG_TEMPLATE, query);

        String answer;
        try {
            var chatClient = clientContext.getBean(ChatClient.class);
            answer = chatClient.prompt(query).call().content();
            logger.info(ANSWER_MSG_TEMPLATE, answer);
        } catch (Exception e) {
            answer = "Error during query: " + e.getMessage();
        } finally {
            // Close the context after the query to avoid a reactor.netty.http.client.PrematureCloseException: Connection prematurely closed DURING response
            close();
        }

        return answer;
    }

    @Override
    public void close() {
        if (isConnected()) {
            logger.info("Closing client context...");
            clientContext.stop();
            clientContext.close();
            logger.info("Client closed gracefully.");
        }
    }
}
