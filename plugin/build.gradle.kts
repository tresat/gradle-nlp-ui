@file:Suppress("UnstableApiUsage")

plugins {
    `java-gradle-plugin`
    id("library-conventions")
    id("test-conventions")
}

dependencies {
    api(project(":client"))
    api(project(":server"))

    testFixturesImplementation(gradleTestKit())
}

gradlePlugin {
    testSourceSets.add(sourceSets["functionalTest"])

    plugins.create("gradleNlpUiPlugin") {
        id = "org.gradle.ai.nlp"
        implementationClass = "org.gradle.ai.nlp.plugin.GradleNlpUiPlugin"
    }
}

description = "This project provides a Gradle plugin for NLP Gradle build introspection."
