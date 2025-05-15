package org.gradle.nlp.plugin.task;

import org.gradle.api.GradleException;
import org.gradle.api.file.Directory;
import org.gradle.api.problems.ProblemGroup;
import org.gradle.api.problems.ProblemId;
import org.gradle.api.problems.Problems;
import org.gradle.api.problems.Severity;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.diagnostics.TaskReportTask;
import org.gradle.work.DisableCachingByDefault;

import javax.inject.Inject;
import java.io.File;

@SuppressWarnings("UnstableApiUsage")
@DisableCachingByDefault(because = "Not worth caching")
public abstract class CustomTasksReport extends TaskReportTask {
    public static final String MCP_REPORTS_DIR = "mcp-reports";

    private final Provider<Directory> outputDir = getProject().getLayout().getBuildDirectory().dir(MCP_REPORTS_DIR);
    private final File outputFile = outputDir.map(dir -> dir.file("custom-tasks-report.txt")).get().getAsFile();

    @Inject
    public CustomTasksReport(Problems problemsService) {
        getOutputs().upToDateWhen(task -> {
            // This task is never up-to-date
            return false;
        });

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

        setOutputFile(outputFile);
    }
}
