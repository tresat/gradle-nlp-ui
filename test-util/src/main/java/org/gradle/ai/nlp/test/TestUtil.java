package org.gradle.ai.nlp.test;

import java.io.IOException;
import java.util.Properties;

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
}
