package org.gradle.ai.nlp.util;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public abstract class Util {
    public static final String API_KEYS_FILE = "api-keys.properties";
    public static final String ANTHROPIC_API_KEY_PROPERTY = "spring.ai.anthropic.api-key";

    private Util() {
        throw new IllegalStateException("Can't instantiate utility class!");
    }

    public static String readAnthropicApiKeyFromProperties() {
        Properties apiKeysProperties = new Properties();
        try (var propStream = Util.class.getClassLoader().getResourceAsStream(API_KEYS_FILE)) {
            if (propStream == null) {
                throw new IllegalStateException(String.format("Properties file '%s' not found in classpath!", API_KEYS_FILE));
            }
            apiKeysProperties.load(propStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!apiKeysProperties.containsKey(ANTHROPIC_API_KEY_PROPERTY)) {
            throw new IllegalStateException(String.format("Property '%s' not found in '%s'!", ANTHROPIC_API_KEY_PROPERTY, API_KEYS_FILE));
        }

        return (String) apiKeysProperties.get(ANTHROPIC_API_KEY_PROPERTY);
    }

    // TODO: replace this with a Groovy extension method?
    public static String relativePathFrom(File file, File baseDir) {
        return new File(baseDir.toPath().relativize(file.toPath()).toString()).toString();
    }

    public static File readableFile(String filePath) {
        return readableFile(filePath, null);
    }

    public static File readableFile(String filePath, @Nullable String fileDesc) {
        if (fileDesc == null) {
            fileDesc = "File";
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalStateException(fileDesc + " does not exist: " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new IllegalStateException(fileDesc + " cannot be read: " + file.getAbsolutePath());
        }
        return file;
    }
}
