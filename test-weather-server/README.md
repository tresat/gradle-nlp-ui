The project builds to `test-weather-server-0.1.0-SNAPSHOT-plain.jar`, but we connect to the Spring Boot `test-weather-server-0.1.0-SNAPSHOT.jar` that has been packaged with Spring Boot via the `bootJar` task.

## Connecting to the Server with Claude Desktop

To connect to the server using Claude Desktop, you need to create/edit the configuration file `~/Library/Application Support/Claude/claude_desktop_config.json` and add the following lines:

```json
{
  "mcpServers": {
    "spring-ai-mcp-weather": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-jar",
        "/<ABSOLUTE_PATH_TO_PROJECT>/server/build/libs/test-weather-server-0.1.0-SNAPSHOT.jar"
      ]
    }
  }
}
```

## Useful References:
- [Spring AI MCP Weather STDIO Server](https://github.com/spring-projects/spring-ai-examples/tree/main/model-context-protocol/weather/starter-stdio-server)
- [Gradle MCP Server](https://github.com/IlyaGulya/gradle-mcp-server)

