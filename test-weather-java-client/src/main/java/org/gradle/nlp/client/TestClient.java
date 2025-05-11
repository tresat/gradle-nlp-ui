package org.gradle.nlp.client;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestClient {
    public static void main(String[] args) {
        var stdioParams = ServerParameters.builder("java")
                .args("-jar", "/Users/ttresansky/Projects/ai/gradle-nlp-ui/test-weather-server/build/libs/server.jar")
                .build();
        var stdioTransport = new StdioClientTransport(stdioParams);
        var mcpClient = McpClient.sync(stdioTransport).build();

        mcpClient.initialize();

        McpSchema.ListToolsResult toolsList = mcpClient.listTools();

        System.out.println();
        System.out.println("Tools available:");
        System.out.println();
        toolsList.tools().forEach(t -> {
            System.out.println("Tool: " + t.name());
            System.out.println("Description: " + t.description());
            System.out.println();
        });

        CallToolResult weather = mcpClient.callTool(
                new CallToolRequest("getWeatherForecastByLocation", Map.of("latitude", "47.6062", "longitude", "-122.3321"))
        );
        CallToolResult alert = mcpClient.callTool(
                new CallToolRequest("getAlerts", Map.of("state", "NY"))
        );

        System.out.println("Forecasts:");
        System.out.println();

        List<String> forecasts = weather.content().stream()
                .filter(c -> c instanceof McpSchema.TextContent)
                .map(McpSchema.TextContent.class::cast)
                .map(McpSchema.TextContent::text)
                .map(t -> t.replaceAll("\"", ""))
                .flatMap(t -> Arrays.stream(t.split("\\\\n")))
                .toList();

        printGroups(forecasts);

        mcpClient.closeGracefully();
    }


    /**
     * Print each string in the list on a new line, adding an extra newline after every fourth item.
     *
     * @param strings The list of strings to print
     */
    public static void printGroups(List<String> strings) {
        for (int i = 0; i < strings.size(); i++) {
            System.out.println(strings.get(i));
            if ((i + 1) % 4 == 0) {
                System.out.println();
            }
        }
    }
}
