plugins {
    application
}

dependencies {
    implementation(project(":test-weather-server"))

    implementation(platform(libs.spring.ai.bom))

    implementation(libs.spring.ai.starter.mcp.client)
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

description = """This project is a Java CLI application that connects to the /server project
and queries the forecastByLocation service, printing the results.
The path to the server jar is hard-coded in the test client's source.
""".trimMargin()

application {
    mainClass = "org.gradle.nlp.client.TestClient"
}
