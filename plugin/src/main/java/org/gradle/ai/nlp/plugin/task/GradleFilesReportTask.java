package org.gradle.ai.nlp.plugin.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.*;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

import static org.gradle.ai.nlp.util.Util.maybeTrimPathPrefix;
import static org.gradle.ai.nlp.util.Util.relativePathFrom;

public abstract class GradleFilesReportTask extends DefaultTask {
    public static final String TASK_NAME = "gradleFilesReport";
    public static final String REPORTS_FILE = "gradle-files-report.txt";

    private final File buildRootDir = getProject().getRootProject().getRootDir();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    public GradleFilesReportTask() {
        getOutputs().upToDateWhen(task -> {
            // This task is never up-to-date
            return false;
        });
    }

    @TaskAction
    public void collectGradleFiles() {
        GradleFileScanner scanner = new GradleFileScanner();
        Set<File> collected = scanner.collectGradleFiles(buildRootDir);

        // TODO: process the files, read the contents, organize by build, etc.
        collected.forEach(f -> {
            String relativePath = relativePathFrom(f, buildRootDir);
        });

        writeToOutputFile(collected);
        getLogger().lifecycle("See the report at: {}", getOutputFile().getAsFile().get().getPath());
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
                files.stream().sorted().forEach(file -> {
                    try {
                        writer.write(maybeTrimPathPrefix(file.getAbsolutePath()) + System.lineSeparator());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Error writing to output file: " + e.getMessage(), e);
        }
    }
}
