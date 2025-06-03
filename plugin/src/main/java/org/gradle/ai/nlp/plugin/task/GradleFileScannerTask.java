package org.gradle.ai.nlp.plugin.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.*;

import static org.gradle.ai.nlp.util.Util.relativePathFrom;

public abstract class GradleFileScannerTask extends DefaultTask {
    public static final String REPORTS_FILE = "gradle-files-report.txt";

    private final File buildRootDir = getProject().getRootProject().getRootDir();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    public GradleFileScannerTask() {
        getOutputs().upToDateWhen(task -> {
            // This task is never up-to-date
            return false;
        });
    }

    @TaskAction
    public void collectGradleFiles() {
        GradleFileScanner scanner = new GradleFileScanner();
        Set<File> collected = scanner.collectGradleFiles(buildRootDir);

        getLogger().lifecycle("Collected Gradle build scripts:");
        collected.forEach(f -> {
            String relativePath = relativePathFrom(f, buildRootDir);
            getLogger().lifecycle(relativePath);
        });

        writeToOutputFile(collected);
    }

    private void writeToOutputFile(Set<File> files) {
        try {
            File outputFile = getOutputFile().getAsFile().get();
            if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
                throw new RuntimeException("Failed to create output directory: " + outputFile.getParent());
            }
            if (!outputFile.exists() && !outputFile.createNewFile()) {
                throw new RuntimeException("Failed to create output file: " + outputFile);
            }

            try (var writer = new java.io.FileWriter(outputFile)) {
                for (File file : files) {
                    writer.write(file.getAbsolutePath() + System.lineSeparator());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error writing to output file: " + e.getMessage(), e);
        }
    }
}
