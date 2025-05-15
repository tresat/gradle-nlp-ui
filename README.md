# gradle-nlp-ui

# Testing the Plugin

## Use the Test Build
The test build is a Gradle build that includes this project and applies the plugin.

From `/test-build`, run `./gradlew startMCP`.

# Testing Other Examples

## Running the Test Chat Client
1. Export your Anthropic API key to the Console you are using:
   ```bash
   export ANTHROPIC_API_KEY=<your_api_key>
   ```
2. Run `./gradlew :test-java-spring-chat-client:bootRun`

## Running the Test Weather Java Client
1. Build the `test-weather-server` project:
   ```bash
   ./gradlew :test-weather-server:build
   ```
2. Run the `test-weather-java-client`:
   ```bash
   ./gradlew :test-weather-java-client:run
   ```

## Testing the Test Weather Server with Claude Desktop
1. Build the `test-weather-server` project:
   ```bash
   ./gradlew :test-weather-server:build
   ```
2. Set your Claude Desktop Config to:
   ```
   {
     "mcpServers": {
       "spring-ai-mcp-weather": {
         "command": "java",
         "args": [
           "-Dspring.ai.mcp.server.stdio=true",
           "-jar",
           "<ABSOLUTE_PATH_TO>/gradle-nlp-ui/test-weather-server/build/libs/test-weather-server.jar"
         ]
       }
     }
   }
   ```
3. Start Claude Desktop and see the `spring-ai-mcp-weather` server in "Search and Tools".
