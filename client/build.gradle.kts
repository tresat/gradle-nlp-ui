@file:Suppress("UnstableApiUsage")

plugins {
    `java-library`
    groovy // For Spock tests
//    id("anthropic-model-conventions")
    id("library-conventions")
}

dependencies {
    implementation(gradleApi())

    implementation(platform(libs.spring.ai.bom))

    implementation(libs.mcp.sdk)
    implementation(libs.spring.ai.starter.mcp.client)

    implementation(libs.guava)
}

/*
 * Gradle provides a SLF4j Provider, and the Spring AI starter also provides one.  Take Spring's.
* SLF4J(W): Class path contains multiple SLF4J providers.
SLF4J(W): Found provider [org.gradle.internal.logging.slf4j.StaticLoggerProvider@2313052e]*/
configurations.all {
    exclude(module = libs.logback.classic.get().module.name)
}

testing {
    suites {
        named<JvmTestSuite>("functionalTest").configure {

            dependencies {
                implementation(project(":server"))
//                runtimeOnly(project(":server")) {
//                    capabilities {
//                        requireCapability("org.gradle.ai.nlp:serverBootJar")
//                    }
//                    because("The functional tests will start the server, and need to grab the boot server jar, not just use the classes")
//                }

                implementation(platform(libs.spring.ai.bom))

                implementation(libs.spring.ai.starter.mcp.server.webmvc) {
                    because("The functional tests will start the server, and need to recieve the server context")
                }
                implementation(libs.spring.web)
                implementation(libs.spring.boot.starter.web)

                implementation(libs.spring.boot.starter.test)
            }

//            targets.all {
//                testTask.configure {
//                    dependsOn("checkAnthropicApiKey")
//                }
//            }
        }
    }
}

description = "This project provides an MCP client built with Spring AI that can be used for NLP Gradle build introspection."

//tasks.register("startServer", ExecServerTask::class) {
//    serverJar = project.layout.projectDirectory.file("../server/build/libs/server-0.1.0.jar")
//}
//
//tasks.named("functionalTest").configure {
//    dependsOn("startServer")
//}
