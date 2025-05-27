@file:Suppress("UnstableApiUsage")

plugins {
    id("spring-boot-conventions")
}

dependencies {
    implementation(platform(libs.spring.ai.bom))

    implementation(libs.mcp.sdk)
    implementation(libs.spring.ai.starter.mcp.server)
    implementation(libs.spring.web)
}

testing {
    suites {
        named<JvmTestSuite>("test").configure {
            targets {
                all {
                    testTask.configure {
                        // Only manually-run tests here
                        failOnNoDiscoveredTests = false
                    }
                }
            }
        }
    }
}

description = "This project showcases the Spring AI MCP Server Boot Starter capabilities with STDIO transport implementation."
