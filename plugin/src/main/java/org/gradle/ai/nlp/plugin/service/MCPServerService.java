package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import org.gradle.ai.nlp.server.McpServerApplication;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.Logging;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ConfigurableApplicationContext;

import org.gradle.api.logging.Logger;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;

public abstract class MCPServerService implements BuildService<MCPServerService.Params>, AutoCloseable {
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

//        verifyGradleProperties();
//        verifyProperties();

        serverContext = McpServerApplication.run(new String[]{});
//        serverContext = McpServerApplication.run(Arrays.asList(
//                "--spring.config.location=classpath:/application.properties",
//                "--logging.file.name=" + getParameters().getLogFile().getAsFile().get().getAbsolutePath(),
//                "--org.gradle.ai.nlp.server.tasks.report.file=" + getParameters().getTasksReportFile().getAsFile().get().getAbsolutePath()
//        ));
        logger.lifecycle(SERVER_STARTUP_MESSAGE);
    }

    private void verifyGradleProperties() {
        System.out.println("LOG FILE: " + getParameters().getLogFile().getAsFile().get().getAbsolutePath());
        System.out.println("TASKS REPORT FILE: " + getParameters().getTasksReportFile().getAsFile().get().getAbsolutePath());
    }

    private void verifyProperties() {
        URI propertiesURI;
        try {
            propertiesURI = MCPServerService.class.getClassLoader().getResource("application.properties").toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        logger.lifecycle(PROPERTIES_MESSAGE, propertiesURI);
        try (InputStream in = MCPServerService.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                String propertiesContent = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                logger.lifecycle(propertiesContent);
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

    interface Params extends BuildServiceParameters {
        RegularFileProperty getTasksReportFile();
        RegularFileProperty getLogFile();
    }
}
