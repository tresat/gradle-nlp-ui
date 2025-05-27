package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import org.gradle.ai.nlp.server.McpServerApplication;
import org.gradle.api.logging.Logging;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class MCPServerService implements BuildService<BuildServiceParameters.@NotNull None>, AutoCloseable {
    public static final String PROPERTIES_MESSAGE = "Using properties file: {}";
    public static final String SERVER_STARTUP_MESSAGE = "Started MCP Server";
    public static final String SERVER_SHUTDOWN_MESSAGE = "Shutdown MCP Server";

    private final Logger logger = Logging.getLogger(MCPClientService.class);

    private ConfigurableApplicationContext serverContext;

    public boolean isStarted() {
        return serverContext != null && serverContext.isActive();
    }

    public void startServer() {
        Preconditions.checkState(!isStarted(), "MCP Server already started");

        verifyProperties();

        serverContext = new SpringApplication(McpServerApplication.class).run(
                "--spring.config.location=classpath:/application.properties"
        );
        logger.lifecycle(SERVER_STARTUP_MESSAGE);
    }

    private void verifyProperties() {
        URI propertiesURI = null;
        try {
            propertiesURI = MCPServerService.class.getClassLoader().getResource("application.properties")
                    .toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        logger.lifecycle(PROPERTIES_MESSAGE, propertiesURI);
        try (InputStream in = MCPServerService.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                String propertiesContent = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                logger.lifecycle("application.properties contents:\n{}", propertiesContent);
            } else {
                logger.lifecycle("application.properties not found in classpath");
            }
        } catch (IOException e) {
            logger.lifecycle("Failed to read application.properties: {}", e.getMessage());
        }
    }

    @Override
    public void close() {
        if (serverContext != null && !serverContext.isClosed()) {
            serverContext.close();
            logger.lifecycle(SERVER_SHUTDOWN_MESSAGE);
        }
    }
}
