# gradle-nlp-ui

# Building and Testing the Plugin

Run the default `build` task via `./gradlew`

# Testing the Plugin in a separate Build

## Use the Test Build
The `test-build` project is a Gradle build that includes the plugin project and applies the plugin for easy testing.

From `/test-build`, run `./gradlew ai --query="What task can I run to make a farm animal fly?"`

# Testing Other Examples

## Running the Test Weather Server
Run the `org.springframework.ai.mcp.sample.server.TestClient` test class.

Or run with Claude Desktop:

1. Build the `test-weather-server` project:
   ```bash
   ./gradlew :test-weather-server:build
   ```
2. Edit your Claude Desktop Config file to:
   ```
   {
     "mcpServers": {
       "spring-ai-mcp-weather": {
         "command": "java",
         "args": [
           "-Dspring.ai.mcp.server.stdio=true",
           "-jar",
           "<ABSOLUTE_PATH_TO>/gradle-nlp-ui/test-weather-server/build/libs/test-weather-server-0.1.0-SNAPSHOT.jar"
         ]
       }
     }
   }
   ```
3. Start Claude Desktop and see the `spring-ai-mcp-weather` server in "Search and Tools"
