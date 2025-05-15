plugins {
    id("org.gradle.ai.nlp")
}

defaultTasks(org.gradle.ai.nlp.plugin.GradleNlpUiPlugin.START_MCP_SERVER_TASK_NAME)
