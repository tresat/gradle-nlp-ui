plugins {
    application
    alias(libs.plugins.spring.boot)
    id("anthropic-model-conventions")
}

dependencies {
    implementation(platform(libs.spring.ai.bom))

    implementation(libs.spring.ai.starter.mcp.client)
}

description = """Test chatbot client using a Spring AI MCP client.
""".trimMargin()

