plugins {
    id("spring-boot-conventions")
    id("anthropic-model-conventions")
}

dependencies {
    api(libs.spring.ai.starter.mcp.server.webmvc)

    implementation(libs.spring.web)
    implementation(libs.logback.classic)
    implementation(libs.spring.boot.starter.actuator)
}

description = "This project provides an MCP server built with Spring AI that can be used for NLP Gradle build introspection."
