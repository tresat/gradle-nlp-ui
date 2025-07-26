package org.gradle.ai.nlp.plugin.task;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.diagnostics.BuildEnvironmentReportTask;
import org.gradle.internal.logging.text.AbstractStyledTextOutput;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.internal.logging.text.StyledTextOutputFactory;
import org.gradle.util.internal.GFileUtils;

import java.io.File;
import java.io.Serializable;

public abstract class CustomBuildEnvironmentReportTask extends BuildEnvironmentReportTask {
    public static final String NAME = "buildEnvironmentReport";
    public static final String DESCRIPTION = "Gathers the output of running the `buildEnvironment` report";

    public static final String REPORTS_FILE_NAME = "custom-build-environment-report.txt";

    private final StyledTextOutput textOutput = new UnstyledStyledTextOutput();
    private final StyledTextOutputFactory textOutputFactory = new UnstyledStyledTextOutputFactory(textOutput);

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @Override
    protected StyledTextOutputFactory getTextOutputFactory() {
        return textOutputFactory;
    }

    public CustomBuildEnvironmentReportTask() {
        // TODO: Remove this when Gradle fixes the issue with configuration cache and overriding @Injected methods in tasks
        notCompatibleWithConfigurationCache("Configuration cache is broken with overriding @Injected methods in tasks");

        doLast(task -> {
            File outputFile = getOutputFile().getAsFile().get();
            GFileUtils.writeFile(textOutput.toString(), outputFile);
            getLogger().lifecycle("See the report at: {}", outputFile.getPath());
        });
    }

    private static final class UnstyledStyledTextOutputFactory implements StyledTextOutputFactory, Serializable {
        private final StyledTextOutput unstyledStyledTextOutput;

        public UnstyledStyledTextOutputFactory(StyledTextOutput unstyledStyledTextOutput) {
            this.unstyledStyledTextOutput = unstyledStyledTextOutput;
        }

        @Override
        public StyledTextOutput create(String logCategory) {
            return unstyledStyledTextOutput;
        }

        @Override
        public StyledTextOutput create(Class<?> logCategory) {
            return unstyledStyledTextOutput;
        }

        @Override
        public StyledTextOutput create(Class<?> logCategory, LogLevel logLevel) {
            return unstyledStyledTextOutput;
        }

        @Override
        public StyledTextOutput create(String logCategory, LogLevel logLevel) {
            return unstyledStyledTextOutput;
        }
    }

    private static final class UnstyledStyledTextOutput extends AbstractStyledTextOutput implements Serializable {
        private final StringBuilder result = new StringBuilder();

        @Override
        public String toString() {
            return result.toString();
        }

        @Override
        protected void doStyleChange(Style style) {
            // No-op, as we are not using styles in this output
        }

        @Override
        protected void doAppend(String text) {
//            System.out.println("Appending text: " + text);
            result.append(text);
        }
    }
}
