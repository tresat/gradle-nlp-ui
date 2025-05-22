plugins {
    application
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(platform(libs.spring.ai.bom))

    implementation(libs.spring.ai.starter.mcp.client)
    implementation(libs.spring.ai.starter.model.anthropic)
}

description = """Test chatbot client using a Spring AI MCP client.
""".trimMargin()

