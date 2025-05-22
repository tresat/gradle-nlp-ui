@file:Suppress("UnstableApiUsage")

plugins {
    `java-library`
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(platform(libs.spring.ai.bom))

    implementation(libs.mcp.sdk)
    implementation(libs.spring.ai.starter.mcp.server)
    implementation(libs.spring.web)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            targets {
                all {
                    testTask.configure {
                        failOnNoDiscoveredTests = false
                    }
                }
            }
        }
    }
}

description = "This project showcases the Spring AI MCP Server Boot Starter capabilities with STDIO transport implementation."
