plugins {
    application
    alias(libs.plugins.spring.boot)
    id("library-conventions")
    id("anthropic-model-conventions")
}

dependencies {
    implementation(libs.spring.ai.starter.mcp.client)
    implementation(libs.spring.ai.client.chat)
    implementation(libs.spring.ai.autoconfigure.model.tool)
}

description = "Test chatbot client using a Spring AI MCP client."
