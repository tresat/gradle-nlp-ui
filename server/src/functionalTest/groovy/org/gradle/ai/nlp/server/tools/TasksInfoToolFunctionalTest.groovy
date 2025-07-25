package org.gradle.ai.nlp.server.tools

import io.modelcontextprotocol.spec.McpSchema
import org.gradle.ai.nlp.server.AbstractMCPServerApplicationWithSyncClientFunctionalTest
import org.gradle.ai.nlp.test.TestUtil
import org.springframework.ai.tool.method.MethodToolCallback

class TasksInfoToolFunctionalTest extends AbstractMCPServerApplicationWithSyncClientFunctionalTest {
    def "server makes tasks info tool available"() {
        when:
        def callbacks = context.getBean(TasksInfoTool.TOOL_NAME).toolCallbacks

        then:
        callbacks.size() == 1
        def tasksInfoTool = callbacks[0] as MethodToolCallback
        tasksInfoTool.toolDefinition.name() == TasksInfoTool.TOOL_NAME
        tasksInfoTool.toolDefinition.description() == TasksInfoTool.TOOL_DESCRIPTION
    }

    def "use tasks info tool"() {
        given:
        mcpClient.initialize()

        when:
        McpSchema.CallToolResult callResult = mcpClient.callTool(new McpSchema.CallToolRequest(TasksInfoTool.TOOL_NAME, [:]))

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
        def reportContents = TestUtil.readResourceFile("sample-mcp-reports/custom-tasks-report.txt")

        then:
        tasks == reportContents
    }
}
