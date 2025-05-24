@file:Suppress("UnstableApiUsage")

plugins {
    `java-gradle-plugin`
    id("library-conventions")
}

dependencies {
    implementation(project(":client"))
    implementation(project(":server"))

    implementation(platform(libs.spring.ai.bom))
    implementation(libs.spring.ai.starter.mcp.server.webmvc)

    implementation(libs.guava)
    implementation(libs.spring.web)

    testFixturesImplementation(gradleTestKit())
    testFixturesImplementation(libs.spock.core)
}

testing {
    suites {
        named<JvmTestSuite>("functionalTest").configure {
            dependencies {
                implementation(testFixtures(project()))
            }
        }
    }
}

gradlePlugin {
    testSourceSets.add(sourceSets["functionalTest"])

    plugins.create("gradleNlpUiPlugin") {
        id = "org.gradle.ai.nlp"
        implementationClass = "org.gradle.ai.nlp.plugin.GradleNlpUiPlugin"
    }
}

description = "This project provides a Gradle plugin for NLP Gradle build introspection."
