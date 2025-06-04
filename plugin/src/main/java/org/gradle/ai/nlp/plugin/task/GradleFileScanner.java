package org.gradle.ai.nlp.plugin.task;

import com.google.common.annotations.VisibleForTesting;

import java.io.File;
import java.io.IOException;
import java.util.*;

/* package */ class GradleFileScanner {
    public Set<File> collectGradleFiles(File rootDir) {
        Set<File> gradleFiles = new LinkedHashSet<>();
        collectGradleFilesRecursive(new ArrayDeque<>(Collections.singleton(rootDir)), gradleFiles, new HashSet<>());
        return gradleFiles;
    }

    private void collectGradleFilesRecursive(Queue<File> buildDirs, Set<File> gradleFiles, Set<File> visitedRootDirs) {
        File currBuildDir = buildDirs.poll();
        if (currBuildDir == null) {
            return;
        }
        if (!visitedRootDirs.add(currBuildDir)) {
            return;
        }

        File settings = getSettingsFile(currBuildDir);
        gradleFiles.add(settings);
        gradleFiles.addAll(findGradleFiles(currBuildDir));

        buildDirs.addAll(findIncludedBuilds(settings));

        collectGradleFilesRecursive(buildDirs, gradleFiles, visitedRootDirs);
    }

    private File getSettingsFile(File dir) {
        File kotlinSettings = new File(dir, "settings.gradle.kts");
        if (kotlinSettings.exists()) {
            return kotlinSettings;
        }

        File groovySettings = new File(dir, "settings.gradle");
        if (groovySettings.exists()) {
            return groovySettings;
        }

        throw new IllegalStateException("No settings.gradle(.kts) found in directory: " + dir);
    }

    // TODO: Is there a most robust solution to walk the build tree?
    @VisibleForTesting
    List<File> findIncludedBuilds(File settingsFile) {
        List<File> includedBuilds = new ArrayList<>();
        // Regex: ^\s*(?:[^;]*;\s*)?includeBuild\s*\((['"])(.+?)\1\)
        // - Only whitespace or (anything then semicolon then whitespace) before includeBuild
        // - Captures the quoted argument
        String pattern = "^\\s*(?:[^;]*;\\s*)?includeBuild\\s*\\((['\"])(.+?)\\1\\)";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        try (Scanner scanner = new Scanner(settingsFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                java.util.regex.Matcher m = regex.matcher(line);
                if (m.find()) {
                    String arg = m.group(2);
                    File included = new File(settingsFile.getParentFile(), arg);
                    if (included.exists()) {
                        includedBuilds.add(included);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading settings file: " + settingsFile, e);
        }

        return includedBuilds;
    }

    public Set<File> findGradleFiles(File dir) {
        Set<File> result = new LinkedHashSet<>();
        findGradleFilesRecursive(dir, result);
        return result;
    }

    private void findGradleFilesRecursive(File dir, Set<File> result) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                findGradleFilesRecursive(file, result);
            } else if (file.getName().endsWith(".gradle") || file.getName().endsWith(".gradle.kts")) {
                result.add(file);
            }
        }
    }
}
