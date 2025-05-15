plugins {
    application

    id("org.springframework.boot").version("3.4.5")
}

dependencies {
    implementation(platform("org.springframework.ai:spring-ai-bom:1.0.0-M8"))
    implementation("org.springframework.ai:spring-ai-starter-mcp-client")
    implementation("org.springframework.ai:spring-ai-starter-model-anthropic")
}

description = """Test chatbot client using a Spring AI MCP client.
""".trimMargin()