package org.gradle.ai.nlp.server.tools

import io.modelcontextprotocol.spec.McpSchema
import org.gradle.ai.nlp.server.AbstractMCPServerApplicationWithSyncClientFunctionalTest
import org.gradle.ai.nlp.test.TestUtil
import org.springframework.ai.tool.method.MethodToolCallback

class ProjectLocationsInfoToolFunctionalTest extends AbstractMCPServerApplicationWithSyncClientFunctionalTest {
    def "server makes project locations tool available"() {
        when:
        def callbacks = context.getBean(ProjectLocationsInfoTool.TOOL_NAME).toolCallbacks

        then:
        callbacks.size() == 1
        def projectLocationsInfoTool = callbacks[0] as MethodToolCallback
        projectLocationsInfoTool.toolDefinition.name() == ProjectLocationsInfoTool.TOOL_NAME
        projectLocationsInfoTool.toolDefinition.description() == ProjectLocationsInfoTool.TOOL_DESCRIPTION
    }

    def "use project locations info tool"() {
        given:
        mcpClient.initialize()

        when:
        McpSchema.CallToolResult callResult = mcpClient.callTool(new McpSchema.CallToolRequest(ProjectLocationsInfoTool.TOOL_NAME, [:]))

        then:
        !callResult.isError()

        when:
        String tasks = callResult.content().stream()
                .filter(c -> c instanceof McpSchema.TextContent)
                .map(McpSchema.TextContent.class::cast)
                .map(McpSchema.TextContent::text)
                .reduce { it -> it }
                .orElseThrow()
                .replace("\\n", "\n")
                .replace('"', '')
        def reportContents = TestUtil.readResourceFile("sample-mcp-reports/project-locations-report.txt")

        then:
        tasks == reportContents
    }
}
