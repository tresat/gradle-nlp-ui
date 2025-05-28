package org.gradle.ai.nlp.plugin;

import com.google.j2objc.annotations.Property;
import org.gradle.api.file.RegularFileProperty;

public abstract class MCPServerExtension {
    @Property
    public abstract RegularFileProperty getLogFile();

    @Property
    public abstract RegularFileProperty getTasksReportFile();
}
