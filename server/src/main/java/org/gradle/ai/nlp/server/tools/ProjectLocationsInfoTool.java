package org.gradle.ai.nlp.server.tools;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import org.gradle.ai.nlp.util.ServerKeys;
import org.gradle.ai.nlp.util.Util;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Component
public final class ProjectLocationsInfoTool {
    public static final String TOOL_NAME = "projectLocationsInfo"; // Should match method name
    public static final String TOOL_DESCRIPTION = "Lists the absolute path to every Gradle file present in this specific build (and any included builds, recursively)";

    private static final String GRADLE_FILES_REPORT_FILE_PATH_VALUE = "${" + ServerKeys.PROJECT_LOCATIONS_REPORT_FILE_PROPERTY + "}";

    @Value(GRADLE_FILES_REPORT_FILE_PATH_VALUE)
    public String gradleFilesReportFilePath;

    @Tool(description = TOOL_DESCRIPTION)
    public String projectLocationsInfo() {
        //noinspection ConstantValue
        Preconditions.checkState(gradleFilesReportFilePath != null, "No Gradle files report file specified.");

        File gradleFilesReportFile = Util.readableFile(gradleFilesReportFilePath, "Gradle files report file");
        try {
            return Files.asCharSource(gradleFilesReportFile, StandardCharsets.UTF_8).read();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read tasks report contents", e);
        }
    }
}
