package org.gradle.ai.nlp.server;

import org.springframework.ai.tool.annotation.Tool;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
public final class TasksInfoTool {
    private Environment environment;

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Tool(description = "Task report information")
    public String tasksInfoTool() {
        String tasksReportFilePath = environment.getProperty("org.gradle.ai.nlp.server.tasks.report.file");
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
