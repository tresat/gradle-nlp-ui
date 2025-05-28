# gradle-nlp-ui

# Building the Plugin

Run the default `build` task via `./gradlew`

# Testing the Plugin

## Use the org.gradle.ai.nlp.client.MCPClientFunctionalTest Build
The test build is a Gradle build that includes this project and applies the plugin.

From `/test-build`, run `./gradlew ai query="What task can I run to make a farm animal fly?"`

# Testing Other Examples

## Running the org.gradle.ai.nlp.client.MCPClientFunctionalTest Chat Client
1. Export your Anthropic API key to the Console you are using:
   ```bash
   export ANTHROPIC_API_KEY=<your_api_key>
   ```
2. Run `./gradlew :test-java-spring-chat-client:bootRun`

## Running the org.gradle.ai.nlp.client.MCPClientFunctionalTest Weather Java Client
1. Build the `test-weather-server` project:
   ```bash
   ./gradlew :test-weather-server:build
   ```
2. Run the `test-weather-java-client`:
   ```bash
   ./gradlew :test-weather-java-client:run
   ```
   The client will automatically start up the server jar in STDIO connection mode.

## Testing the org.gradle.ai.nlp.client.MCPClientFunctionalTest Weather Server with Claude Desktop
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
3. Start Claude Desktop and see the `spring-ai-mcp-weather` server in "Search and Tools"


# Troubleshooting

If you get a message like:
```
> Task :mcpStartServer FAILED
Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'anthropicApi' defined in class path resource [org/springframework/ai/model/anthropic/autoconfigure/AnthropicChatAutoConfiguration.class]: Failed to instantiate [org.springframework.ai.anthropic.api.AnthropicApi]: Factory method 'anthropicApi' threw exception with message: apiKey cannot be null
Application run failed
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'anthropicApi' defined in class path resource [org/springframework/ai/model/anthropic/autoconfigure/AnthropicChatAutoConfiguration.class]: Failed to instantiate [org.springframework.ai.anthropic.api.AnthropicApi]: Factory method 'anthropicApi' threw exception with message: apiKey cannot be null
```

Then you probably need to export your Anthropic API key to the environment variable `ANTHROPIC_API_KEY` (or set it in the `application.properties` file).

**Don't commit your API key to the repository!**
