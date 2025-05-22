plugins {
    `java-library`
    groovy // For Spock tests
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


description = "This project provides an MCP server built with Spring AI that can be used for NLP Gradle build introspection."
