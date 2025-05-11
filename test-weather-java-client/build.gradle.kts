plugins {
    application
}

dependencies {
    implementation(platform("org.springframework.ai:spring-ai-bom:1.0.0-SNAPSHOT"))

    implementation("org.springframework.ai:spring-ai-starter-mcp-client")

    implementation(project(":test-weather-server"))
}

description = """This project is a Java CLI application that connects to the /server project
and queries the forecastByLocation service, printing the results.
The path to the server jar is hard-coded in the test client's source.
""".trimMargin()