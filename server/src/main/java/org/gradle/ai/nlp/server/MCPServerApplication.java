package org.gradle.ai.nlp.server;

import com.google.common.annotations.VisibleForTesting;
import org.gradle.ai.nlp.exception.MissingRequiredPropertiesException;
import org.gradle.ai.nlp.util.Util;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.util.List;

@SpringBootApplication
public class MCPServerApplication {
    public static final String SERVER_PORT_PROPERTY = "server.port";
    public static final String ANTHROPIC_API_KEY_PROPERTY = Util.ANTHROPIC_API_KEY_PROPERTY;
    public static final String LOG_FILE_PROPERTY = "logging.file.name";

    public static final String TASKS_REPORT_FILE_PROPERTY = "org.gradle.ai.nlp.server.reports.tasks.file";
    public static final String GRADLE_FILES_REPORT_FILE_PROPERTY = "org.gradle.ai.nlp.server.reports.gradle.file";

    public static final List<String> REQUIRED_PROPERTIES = List.of(SERVER_PORT_PROPERTY, ANTHROPIC_API_KEY_PROPERTY, LOG_FILE_PROPERTY, TASKS_REPORT_FILE_PROPERTY, GRADLE_FILES_REPORT_FILE_PROPERTY);

    public static void main(String[] args) {
        run(args);
    }

    public static ConfigurableApplicationContext run(int port, String anthropicApiKey, File logFile, File tasksReportFile, File gradleFilesReportFile) {
        var args = new String[]{
                "--" + SERVER_PORT_PROPERTY + "=" + port,
                "--" + LOG_FILE_PROPERTY + "=" + logFile.getAbsolutePath(),
                "--" + ANTHROPIC_API_KEY_PROPERTY + "=" + anthropicApiKey,
                "--" + TASKS_REPORT_FILE_PROPERTY + "=" + tasksReportFile.getAbsolutePath(),
                "--" + GRADLE_FILES_REPORT_FILE_PROPERTY + "=" + gradleFilesReportFile.getAbsolutePath(),
        };
        return run(args);
    }

    private static ConfigurableApplicationContext run(String[] args) {
        verifyArgs(args);
        return SpringApplication.run(MCPServerApplication.class, args);
    }

    @VisibleForTesting
    static void verifyArgs(String[] args) {
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

    @Bean
    public ToolCallbackProvider tasksInfo(TasksInfoTool tasksInfoTool) {
        return MethodToolCallbackProvider.builder().toolObjects(tasksInfoTool).build();
    }

    @Bean
    public ToolCallbackProvider gradleFiles(GradleFilesTool gradleFilesTool) {
        return MethodToolCallbackProvider.builder().toolObjects(gradleFilesTool).build();
    }

//    @Bean
//    public FunctionToolCallback<String, String> gradleFileContents() {
//        return FunctionToolCallback
//                .builder("readFile", new ReadGradleFileService())
//                .description("Read the contents of a Gradle file identified by its absolute path")
//                .inputType(String.class)
//                .build();
//    }
}
