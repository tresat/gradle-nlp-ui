plugins {
    id("org.gradle.ai.nlp")
}

tasks.register("flyPlane") {
    description = "A task that simulates flying a plane."
}

tasks.register("flyPig") {
    description = "A task that simulates flying a pig."
}
