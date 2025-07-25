package org.gradle.ai.nlp.test;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import java.io.InputStreamReader;

public abstract class TestUtil {
    private static final String TEST_PROPERTIES_FILE = "application-test.properties";
    private static final String PORT_PROPERTY = "server.port";

    private TestUtil() {
        throw new IllegalStateException("Can't instantiate utility class!");
    }

    public static int readPortFromProperties() {
        Properties testProperties = new Properties();
        try (var propStream = TestUtil.class.getClassLoader().getResourceAsStream(TEST_PROPERTIES_FILE)) {
            if (propStream == null) {
                throw new IllegalStateException(String.format("Properties file '%s' not found in classpath!", TEST_PROPERTIES_FILE));
            }
            testProperties.load(propStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!testProperties.containsKey(PORT_PROPERTY)) {
            throw new IllegalStateException(String.format("Property '%s' not found in '%s'!", PORT_PROPERTY, TEST_PROPERTIES_FILE));
        }

        return Integer.parseInt((String) testProperties.get(PORT_PROPERTY));
    }

    /**
     * Reads a text resource file from the classpath and returns its content as a String.
     * <p>
     * The resource file should be present directly on the classpath, <strong>NOT</strong> inside a JAR file on the classpath.
     *
     * @param resourceFileRelativePath the relative path to the resource file (e.g., 'sample1/sample1.txt') from the classpath root
     * @return the content of the resource file as a String
     */
    public static String readResourceFile(String resourceFileRelativePath) {
        var resource = TestUtil.class.getClassLoader().getResource(resourceFileRelativePath);
        if (resource == null) {
            throw new IllegalArgumentException("Resource file '" + resourceFileRelativePath + "' not found.");
        }

        try {
            return Files.readString(new File(resource.toURI()).toPath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read resource file: " + resource, e);
        }
    }

    /**
     * Copies a resource directory from the classpath into the given destination directory.
     * <p>
     * The directory can be present directly on the classpath, or inside a JAR file on the classpath.
     *
     * @param resourceDirName the name of the directory in src/test/resources to copy (e.g., 'sample1')
     * @param destDir the destination directory (e.g., tempDir)
     */
    public static void copyResourceDir(String resourceDirName, File destDir) {
        var resource = TestUtil.class.getClassLoader().getResource(resourceDirName);
        if (resource == null) {
            throw new IllegalArgumentException("Resource directory '" + resourceDirName + "' not found.");
        }

        try {
            if ("jar".equals(resource.getProtocol())) {
                extractFromJar(resourceDirName, destDir, resource);
            } else {
                File resourceFile = new File(resource.toURI());
                copyDirectory(resourceFile, destDir);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy resource directory: " + resource, e);
        }
    }

    private static void extractFromJar(String resourceDirName, File destDir, URL resource) throws IOException {
        var jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
        try (var jarFile = new java.util.jar.JarFile(new File(jarPath))) {
            var entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (entry.getName().startsWith(resourceDirName) && !entry.isDirectory()) {
                    var inputStream = jarFile.getInputStream(entry);
                    var destFile = new File(destDir, entry.getName().substring(resourceDirName.length() + 1));
                    destFile.getParentFile().mkdirs();
                    Files.copy(inputStream, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            if (!target.mkdirs()) {
                throw new IOException("Failed to create target directory: " + target);
            }
        }
        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                File destFile = new File(target, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, destFile);
                } else {
                    Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    public static boolean isAnthropicAvailable() {
        String urlStr = "https://status.anthropic.com/api/v2/status.json";
        try {
            var url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                 JsonReader reader = Json.createReader(isr)) {
                JsonObject root = reader.readObject();
                JsonObject status = root.getJsonObject("status");
                String indicator = status.getString("indicator", "");
                // "none" means no outage, "major" or "critical" or anything else means outage
                return "none".equalsIgnoreCase(indicator);
            }
        } catch (Exception e) {
            // If unable to check, assume not available
            return false;
        }
    }
}
