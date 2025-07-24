# gradle-nlp-ui

# Building and Testing the Plugin

Run the default `build` task via `./gradlew`

# Testing the Plugin in a separate Build

## Use the Test Build
The `test-build` project is a Gradle build that includes the plugin project and applies the plugin for easy testing.

From `/test-build`, here are some sample queries to run that the server should be able to answer:
- `./gradlew ai --query="What task can I run to make a farm animal fly?"`
- `./gradlew ai --query="List the paths to all the gradle build files in this build where the project dir is not in the default location.  Response in the following format: Project X has a non-default location of Y"`


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

# Troubleshooting

## apiKey cannot be null

If you get a message like:
```
> Task :mcpStartServer FAILED
Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'anthropicApi' defined in class path resource [org/springframework/ai/model/anthropic/autoconfigure/AnthropicChatAutoConfiguration.class]: Failed to instantiate [org.springframework.ai.anthropic.api.AnthropicApi]: Factory method 'anthropicApi' threw exception with message: apiKey cannot be null
Application run failed
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'anthropicApi' defined in class path resource [org/springframework/ai/model/anthropic/autoconfigure/AnthropicChatAutoConfiguration.class]: Failed to instantiate [org.springframework.ai.anthropic.api.AnthropicApi]: Factory method 'anthropicApi' threw exception with message: apiKey cannot be null
```

Then you probably need to export your Anthropic API key to the environment variable `ANTHROPIC_API_KEY` (or set it in the `application.properties` file).

**Don't commit your API key to the repository!**

## HTTP 529 Overloaded

If you get a message like:
```
org.springframework.ai.retry.TransientAiException: HTTP 529 - {"type":"error","error":{"type":"overloaded_error","message":"Overloaded"}}
	at org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration$2.handleError(SpringAiRetryAutoConfiguration.java:119) ~[spring-ai-autoconfigure-retry-1.0.0.jar:1.0.0]
	at org.springframework.web.client.ResponseErrorHandler.handleError(ResponseErrorHandler.java:58) ~[spring-web-6.2.6.jar:6.2.6]
	at org.springframework.web.client.StatusHandler.lambda$fromErrorHandler$1(StatusHandler.java:71) ~[spring-web-6.2.6.jar:6.2.6]
	at org.springframework.web.client.StatusHandler.handle(StatusHandler.java:146) ~[spring-web-6.2.6.jar:6.2.6]
```

This is probably an error from the Anthropic API indicating that the service is overloaded.
Check https://status.anthropic.com/ to confirm this.
