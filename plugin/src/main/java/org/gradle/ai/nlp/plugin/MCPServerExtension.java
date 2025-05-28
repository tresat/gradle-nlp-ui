package org.gradle.ai.nlp.plugin;

import org.gradle.api.provider.Property;
import org.gradle.api.file.RegularFileProperty;

public interface MCPServerExtension {
    Property<Integer> getPort();
    RegularFileProperty getLogFile();
}
