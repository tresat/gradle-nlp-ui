plugins {
    id("org.gradle.ai.nlp")
}

mcpServer {
    port = 8100
    anthropicApiKey = project.providers.gradleProperty("ANTHROPIC_API_KEY")
        .orElse(project.providers.environmentVariable("ANTHROPIC_API_KEY"))
}

tasks.register("flyRocket") {
    description = "Rockets fly higher than anything else."
}

tasks.register("flySuperman") {
    description = "Able to leap tall buildings in a single bound."
}

tasks.register("flyAirplane") {
    description = "A task that simulates flying a plane."
}

tasks.register("flyPig") {
    description = "A task that simulates flying a pig."
}

// Whoops, this build contains a secret: "SuperSeCrEtKey123"
description = "A Gradle build script that simulates flying various objects."