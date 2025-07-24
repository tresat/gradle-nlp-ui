package org.gradle.ai.nlp.server;

import com.google.common.annotations.VisibleForTesting;
import org.gradle.ai.nlp.exception.MissingRequiredPropertiesException;
import org.gradle.ai.nlp.server.tools.ProjectLocationsInfoTool;
import org.gradle.ai.nlp.server.tools.TasksInfoTool;
import org.gradle.ai.nlp.util.ServerKeys;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;

import java.io.File;
import java.util.function.Function;

@SpringBootApplication
public class MCPServerApplication implements ServerKeys {
    public static final String GRADLE_FILE_CONTENTS_TOOL_NAME = "gradleFileContents";

    public static void main(String[] args) {
        run(args);
    }

    public static ConfigurableApplicationContext run(int port, String anthropicApiKey, File logFile, File tasksReportFile, File projectLocationsReportFile) {
        var args = new String[]{
                "--" + SERVER_PORT_PROPERTY + "=" + port,
                "--" + LOG_FILE_PROPERTY + "=" + logFile.getAbsolutePath(),
                "--" + ANTHROPIC_API_KEY_PROPERTY + "=" + anthropicApiKey,
                "--" + TASKS_REPORT_FILE_PROPERTY + "=" + tasksReportFile.getAbsolutePath(),
                "--" + PROJECT_LOCATIONS_REPORT_FILE_PROPERTY + "=" + projectLocationsReportFile.getAbsolutePath(),
        };
        return run(args);
    }

    private static ConfigurableApplicationContext run(String[] args) {
        verifyArgs(args);
        return SpringApplication.run(MCPServerApplication.class, args);
    }

    @VisibleForTesting
    static void verifyArgs(String[] args) {
        //noinspection ConstantValue
        if (args == null || args.length < REQUIRED_PROPERTIES.size()) {
            throw new MissingRequiredPropertiesException(REQUIRED_PROPERTIES, args);
        }

        for (String requiredArg : REQUIRED_PROPERTIES) {
            boolean found = false;
            for (String arg : args) {
                if (arg.startsWith("--" + requiredArg + "=")) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new MissingRequiredPropertiesException(REQUIRED_PROPERTIES, args);
            }
        }
    }

    // Note that the names of the @Bean methods are used as the tool names in the client, so they should be descriptive.
    // They must also be unique vs. the names of the @Tool methods in the tool classes.
    // TODO: Consider Constants for the tool names to avoid duplication: https://docs.spring.io/spring-ai/reference/api/tools.html#_dynamic_specification_bean

    @Bean(TasksInfoTool.TOOL_NAME)
    public ToolCallbackProvider tasksInfo(TasksInfoTool tasksInfoTool) {
        return MethodToolCallbackProvider.builder().toolObjects(tasksInfoTool).build();
    }

    @Bean(ProjectLocationsInfoTool.TOOL_NAME)
    public ToolCallbackProvider projectLocationsInfo(ProjectLocationsInfoTool projectLocationsInfoTool) {
        return MethodToolCallbackProvider.builder().toolObjects(projectLocationsInfoTool).build();
    }

    @Bean(GRADLE_FILE_CONTENTS_TOOL_NAME)
    @Description("Read the contents of a Gradle file identified by its absolute path")
    public Function<String, String> gradleFileContents() {
        return new ReadGradleFileService();
    }
}
