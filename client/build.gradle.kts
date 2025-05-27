plugins {
    id("library-conventions")
    id("test-conventions")
}

dependencies {
    implementation(libs.spring.ai.starter.mcp.client)
}

description = "This project provides an MCP client built with Spring AI that can be used for NLP Gradle build introspection."
