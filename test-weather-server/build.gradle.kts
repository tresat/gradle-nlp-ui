@file:Suppress("UnstableApiUsage")

plugins {
    id("spring-boot-conventions")
}

dependencies {
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

description = """This project showcases the Spring AI MCP Server Boot Starter capabilities with STDIO transport implementation.
    
Run the class in /test to see a sample Java CLI application that connects to the server
and queries the forecastByLocation service, printing the results.
The path to the server jar is hard-coded in the test client's source.
""".trimMargin()
