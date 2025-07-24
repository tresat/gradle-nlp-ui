package org.gradle.ai.nlp.util;

import java.util.List;

public interface ServerKeys {
    String SERVER_PORT_PROPERTY = "server.port";
    String ANTHROPIC_API_KEY_PROPERTY = Util.ANTHROPIC_API_KEY_PROPERTY;
    String LOG_FILE_PROPERTY = "logging.file.name";

    String TASKS_REPORT_FILE_PROPERTY = "org.gradle.ai.nlp.server.reports.tasks.file";
    String PROJECT_LOCATIONS_REPORT_FILE_PROPERTY = "org.gradle.ai.nlp.server.reports.gradle.file";

    List<String> REQUIRED_PROPERTIES = List.of(SERVER_PORT_PROPERTY, ANTHROPIC_API_KEY_PROPERTY, LOG_FILE_PROPERTY, TASKS_REPORT_FILE_PROPERTY, PROJECT_LOCATIONS_REPORT_FILE_PROPERTY);

    String LISTENING_ON = "MCP server is listening on: ";
}
