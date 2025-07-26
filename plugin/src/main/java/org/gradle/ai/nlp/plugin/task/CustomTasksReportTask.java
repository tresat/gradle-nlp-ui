package org.gradle.ai.nlp.plugin.task;

import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.diagnostics.TaskReportTask;

import java.io.File;

public abstract class CustomTasksReportTask extends TaskReportTask {
    public static final String NAME = "tasksReport";
    public static final String DESCRIPTION = "Gathers the output of running the `tasks` report";

    public static final String REPORTS_FILE_NAME = "custom-tasks-report.txt";

    private final Provider<Directory> outputDir = getProject().getLayout().getBuildDirectory().dir(GradleNlpUiPlugin.MCP_REPORTS_DIR);
    private final Provider<RegularFile> outputFile = outputDir.map(dir -> dir.file(REPORTS_FILE_NAME));

    @OutputFile
    public abstract RegularFileProperty getRegularOutputFile();

    public CustomTasksReportTask() {
        File rawOutputFile = outputFile.get().getAsFile();

        setOutputFile(rawOutputFile);
        getRegularOutputFile().set(outputFile);
        setShowDetail(true);
    }
}
