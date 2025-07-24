package org.gradle.ai.nlp.server.tools;

import com.google.common.base.Preconditions;
import org.gradle.ai.nlp.util.ServerKeys;
import org.springframework.ai.tool.annotation.Tool;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Component
public final class TasksInfoTool {
    public static final String TOOL_NAME = "tasksInfo"; // Should match method name
    public static final String TOOL_DESCRIPTION = "Provides the result of running the tasks task to report information on this specific build's available tasks";

    private static final String TASKS_REPORT_FILE_PATH_VALUE = "${" + ServerKeys.TASKS_REPORT_FILE_PROPERTY + "}";

    @Value(TASKS_REPORT_FILE_PATH_VALUE)
    public String tasksReportFilePath;

    @Tool(description = TOOL_DESCRIPTION)
    public String tasksInfo() {
        //noinspection ConstantValue
        Preconditions.checkState(tasksReportFilePath != null, "No tasks report file specified.");

        File tasksReportFile = new File(tasksReportFilePath);
        if (!tasksReportFile.exists()) {
            throw new IllegalStateException("Tasks report file does not exist: " + tasksReportFile.getAbsolutePath());
        }

        try {
            return Files.asCharSource(tasksReportFile, StandardCharsets.UTF_8).read();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read tasks report contents", e);
        }
    }
}
