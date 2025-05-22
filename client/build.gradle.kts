plugins {
    `java-library`
    groovy // For Spock tests
}

dependencies {
    implementation(gradleApi())

    implementation(platform(libs.spring.ai.bom))

    implementation(libs.mcp.sdk)
    implementation(libs.spring.ai.starter.mcp.client)
    implementation(libs.spring.ai.starter.model.anthropic)
    implementation(libs.spring.web)
}

description = "This project provides an MCP client built with Spring AI that can be used for NLP Gradle build introspection."
