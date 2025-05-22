package org.gradle.ai.nlp.plugin.task;

import org.gradle.api.GradleException;
import org.gradle.api.file.Directory;
import org.gradle.api.problems.ProblemGroup;
import org.gradle.api.problems.ProblemId;
import org.gradle.api.problems.Problems;
import org.gradle.api.problems.Severity;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.diagnostics.TaskReportTask;

import javax.inject.Inject;
import java.io.File;

@SuppressWarnings("UnstableApiUsage")
public abstract class CustomTasksReportTask extends TaskReportTask {
    public static final String MCP_REPORTS_DIR = "mcp-reports";
    public static final String REPORTS_FILE = "custom-tasks-report.txt";

    private final Provider<Directory> outputDir = getProject().getLayout().getBuildDirectory().dir(MCP_REPORTS_DIR);
    private final File outputFile = outputDir.map(dir -> dir.file(REPORTS_FILE)).get().getAsFile();

    @Inject
    public CustomTasksReportTask(Problems problemsService) {
        setOutputFile(outputFile);

        doFirst(task -> {
            try {
                if (!outputFile.exists()) {
                    if (!outputDir.get().getAsFile().mkdirs()) {
                        throw new GradleException("Failed to create output directory: " + outputDir.get().getAsFile());
                    }
                }

                if (!outputFile.exists()) {
                    if (!outputFile.createNewFile()) {
                        throw new GradleException("Failed to create output file: " + outputFile);
                    }
                }
            } catch (Exception e) {
                ProblemId id = ProblemId.create("io-error", "IO Error", ProblemGroup.create("mcp-plugin", "mcp-plugin"));
                throw problemsService.getReporter().throwing(e, id, spec -> {
                    spec.severity(Severity.ERROR);
                });
            }
        });
    }
}
