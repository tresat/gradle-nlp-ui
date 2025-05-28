plugins {
    id("spring-boot-conventions")
    id("anthropic-model-conventions")
}

dependencies {
    api(libs.spring.ai.starter.mcp.server.webmvc)

    api(libs.spring.web)
    api(libs.spring.boot.starter.actuator)
}

description = """This project provides an MCP server built with Spring AI that can be used for NLP Gradle build introspection." +
This server is ignorant of Gradle, so it can be run externally to a build for testing purposes."""
