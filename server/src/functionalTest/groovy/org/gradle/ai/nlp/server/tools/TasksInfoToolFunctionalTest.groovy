package org.gradle.ai.nlp.server.tools

import org.gradle.ai.nlp.server.AbstractMCPServerApplicationFunctionalTest
import org.gradle.ai.nlp.server.MCPServerApplication
import org.springframework.ai.tool.method.MethodToolCallback

class TasksInfoToolFunctionalTest extends AbstractMCPServerApplicationFunctionalTest {
    def "server makes tasks info tool available"() {
        when:
        def callbacks = context.getBean(TasksInfoTool.TOOL_NAME).toolCallbacks

        then:
        callbacks.size() == 1
        def tasksInfoTool = callbacks[0] as MethodToolCallback
        tasksInfoTool.toolDefinition.name() == TasksInfoTool.TOOL_NAME
        tasksInfoTool.toolDefinition.description() == TasksInfoTool.TOOL_DESCRIPTION
    }
}
