package org.gradle.ai.nlp.server;

import org.springframework.ai.tool.annotation.Tool;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public final class TasksInfoTool {
    @Value("${org.gradle.ai.nlp.server.tasks.report.file}")
    public String tasksReportFilePath;

    @Tool(description = "Task report information")
    public String tasksInfoTool() {
        if (null != tasksReportFilePath) {
            try {
                var tasksReportFile = new java.io.File(tasksReportFilePath);
                return Files.asCharSource(tasksReportFile, StandardCharsets.UTF_8).read();
            } catch (Exception e) {
                throw new RuntimeException("Failed to read tasks report contents", e);
            }
        } else {
            return "No tasks report file specified.";
        }
    }
}
