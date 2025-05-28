plugins {
    id("library-conventions")
    id("test-conventions")
    id("anthropic-model-conventions")
}

dependencies {
    implementation(libs.spring.ai.starter.mcp.client)
}

description = """This project provides an MCP client built with Spring AI.  This project is ignorant of Gradle, 
and can be run externally to a build to connect to an MCP server."""
