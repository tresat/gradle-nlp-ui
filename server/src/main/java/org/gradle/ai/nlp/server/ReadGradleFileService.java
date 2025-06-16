package org.gradle.ai.nlp.server;

import com.google.common.io.Files;
import org.gradle.ai.nlp.util.Util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public final class ReadGradleFileService implements Function<String, String> {
    public String apply(String requestedFilePath) {
        try {
            File requestedFile = Util.readableFile(requestedFilePath, "Gradle file");
            return Files.asCharSource(requestedFile, StandardCharsets.UTF_8).read();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read Gradle file: " + requestedFilePath, e);
        }
    }
}
