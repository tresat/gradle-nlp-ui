plugins {
    `java-library`
    id("org.springframework.boot").version("3.1.0")
}

dependencies {
    implementation(libs.mcp.sdk)

    implementation(platform("org.springframework.ai:spring-ai-bom:1.0.0-M8"))

    implementation("org.springframework.ai:spring-ai-starter-mcp-server")
    implementation("org.springframework:spring-web:6.2.6")
}

description = "This project showcases the Spring AI MCP Server Boot Starter capabilities with STDIO transport implementation."
