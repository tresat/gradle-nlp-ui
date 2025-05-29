plugins {
    id("org.gradle.ai.nlp")
}

mcpServer {
    anthropicApiKey = project.providers.gradleProperty("ANTHROPIC_API_KEY")
        .orElse(project.providers.environmentVariable("ANTHROPIC_API_KEY"))
}

tasks.register("flyPlane") {
    description = "A task that simulates flying a plane."
}

tasks.register("flyPig") {
    description = "A task that simulates flying a pig."
}

tasks.register("flySuperman") {
    description = "Able to leap tall buildings in a single bound."
}
