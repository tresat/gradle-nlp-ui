package org.gradle.ai.nlp.plugin.task;

import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin;
import org.gradle.api.GradleException;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.problems.ProblemGroup;
import org.gradle.api.problems.ProblemId;
import org.gradle.api.problems.Problems;
import org.gradle.api.problems.Severity;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.diagnostics.TaskReportTask;

import javax.inject.Inject;
import java.io.File;

@SuppressWarnings("UnstableApiUsage")
public abstract class CustomTasksReportTask extends TaskReportTask {
    public static final String NAME = "tasksReport";
    public static final String DESCRIPTION = "Gathers the output of running the `tasks` report";

    public static final String REPORTS_FILE_NAME = "custom-tasks-report.txt";

    private final Provider<Directory> outputDir = getProject().getLayout().getBuildDirectory().dir(GradleNlpUiPlugin.MCP_REPORTS_DIR);
    private final Provider<RegularFile> outputFile = outputDir.map(dir -> dir.file(REPORTS_FILE_NAME));

    @OutputFile
    public abstract RegularFileProperty getRegularOutputFile();

    @Inject
    public abstract Problems getProblemsService();

    public CustomTasksReportTask() {
        File rawOutputFile = outputFile.get().getAsFile();

        setOutputFile(rawOutputFile);
        getRegularOutputFile().set(outputFile);
        setShowDetail(true);

        doFirst(task -> {
            try {
                if (!rawOutputFile.exists()) {
                    if (!outputDir.get().getAsFile().mkdirs()) {
                        throw new GradleException("Failed to create output directory: " + outputDir.get().getAsFile());
                    }
                }

                if (!rawOutputFile.exists()) {
                    if (!rawOutputFile.createNewFile()) {
                        throw new GradleException("Failed to create output file: " + outputFile);
                    }
                }
            } catch (Exception e) {
                ProblemId id = ProblemId.create("report-file-io-error", "Error Creating Report File", ProblemGroup.create("mcp-plugin", "mcp-plugin"));
                throw getProblemsService().getReporter().throwing(e, id, spec -> spec.severity(Severity.ERROR));
            }
        });
    }
}
