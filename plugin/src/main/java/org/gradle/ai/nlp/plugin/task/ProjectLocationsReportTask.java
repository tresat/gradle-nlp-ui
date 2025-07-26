package org.gradle.ai.nlp.plugin.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.internal.project.ProjectRegistry;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.initialization.DefaultProjectDescriptor;
import org.gradle.util.internal.GFileUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ProjectLocationsReportTask extends DefaultTask {
    public static final String TASK = "projectsLocationsReport";
    public static final String DESCRIPTION = "Collects information about Gradle projects and build scripts from the build";

    public static final String REPORTS_FILE_NAME = "project-locations-report.txt";

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    private final Provider<File> settingsFile = getProject().getProviders().provider(() -> ((GradleInternal) getProject().getGradle()).getSettings().getBuildscript().getSourceFile());
    private final Set<ProjectInfo> projectInfos = new HashSet<>();

    @SuppressWarnings("CodeBlock2Expr")
    public ProjectLocationsReportTask() {
        ProjectRegistry<DefaultProjectDescriptor> projectRegistry = ((GradleInternal) getProject().getGradle()).getSettings().getProjectRegistry();
        Map<ProjectDescriptor, ProjectInfo> projectInfoBuilder = new HashMap<>();

        projectRegistry.getAllProjects().forEach(projectDescriptor -> {
            projectInfoBuilder.put(
                    projectDescriptor,
                    new ProjectInfo(
                            ":".equals(projectDescriptor.getPath()),
                            projectDescriptor.getName(),
                            projectDescriptor.getBuildFile(),
                            projectDescriptor.getProjectDir().toPath()
                    )
            );
        });

        projectRegistry.getAllProjects().forEach(parent -> {
            parent.children().forEach(child -> {
                projectInfoBuilder.get(parent).addChild(projectInfoBuilder.get(child));
            });
        });

        projectInfos.addAll(projectInfoBuilder.values());
    }

    @TaskAction
    public void report() {
        GFileUtils.writeFile(
            buildReportContents(),
            getOutputFile().get().getAsFile()
        );

        getLogger().lifecycle("See the report at: {}", getOutputFile().getAsFile().get().getPath());
    }

    private String buildReportContents() {
        return "Gradle Project Locations Report\n" +
                "Settings: " + settingsFile.get().toPath() + "\n\n" +
                projectInfos.stream().map(ProjectInfo::report).collect(Collectors.joining("\n"));
    }

    private static final class ProjectInfo {
        private final boolean isRoot;
        private final String name;
        private final File buildFile;
        private final Path projectDir;
        private final Set<ProjectInfo> children = new HashSet<>();

        private ProjectInfo(boolean isRoot, String name, File buildFile, Path projectDir) {
            this.isRoot = isRoot;
            this.name = name;
            this.buildFile = buildFile;
            this.projectDir = projectDir;
        }

        public void addChild(ProjectInfo child) {
            children.add(child);
        }

        public String report() {
            StringBuilder sb = new StringBuilder();
            if (isRoot) {
                sb.append("Root ");
            }
            sb.append("Project: ").append(name).append("\n");
            sb.append("Build File: ").append(buildFile).append("\n");
            sb.append("Project Directory: ").append(projectDir).append("\n");
            if (!children.isEmpty()) {
                sb.append("Children:\n");
                for (ProjectInfo child : children) {
                    sb.append("  - ").append(child.name).append("\n");
                }
            }
            return sb.toString();
        }
    }
}
