tasks.register("checkAnthropicApiKey", org.gradle.ai.nlp.build.task.CheckAnthropicApiKeyTask::class) {
    group = "Verification"
    description = "Checks if ANTHROPIC_API_KEY is set as a project property or environment variable."
}

tasks.named("check") {
    dependsOn("checkAnthropicApiKey")
}
