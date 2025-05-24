@file:Suppress("UnstableApiUsage")

plugins {
    `java-library`
    groovy // For Spock tests
    alias(libs.plugins.spring.boot)
    id("library-conventions")
}

dependencies {
    implementation(platform(libs.spring.ai.bom))

    implementation(libs.mcp.sdk)
    implementation(libs.spring.ai.starter.mcp.server.webmvc)
    implementation(libs.spring.web)
    implementation(libs.logback.classic)
    implementation(libs.spring.boot.starter.actuator)
}

testing {
    suites {
        withType<JvmTestSuite> {
            targets {
                all {
                    testTask.configure {
                        failOnNoDiscoveredTests = false
                    }
                }
            }
        }

        named<JvmTestSuite>("functionalTest").configure {
            dependencies {
                //implementation(project(":client"))

                implementation(libs.spring.boot.starter.web)
                implementation(libs.spring.boot.starter.test)
            }
        }
    }
}

// Modify the bootArchives variant to allow it to be easily selected via dependency resolution by the client project
configurations.named("apiElements").configure {
    outgoing.capability("org.gradle.ai.nlp:serverBootJar:${project.version}")
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
        attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EMBEDDED))
    }
}

description = "This project provides an MCP server built with Spring AI that can be used for NLP Gradle build introspection."

tasks.named("bootJar").configure {
    outputs.upToDateWhen { false }
}
