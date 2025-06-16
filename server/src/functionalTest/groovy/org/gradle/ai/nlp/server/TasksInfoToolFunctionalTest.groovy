package org.gradle.ai.nlp.server

import org.springframework.ai.tool.method.MethodToolCallback

class TasksInfoToolFunctionalTest extends AbstractMCPServerApplicationFunctionalTest {
    def "server makes tasks info tool available"() {
        when:
        def callbacks = context.getBean(MCPServerApplication.TASKS_INFO_TOOL_NAME).toolCallbacks

        then:
        callbacks.size() == 1
        def tasksInfoTool = callbacks[0] as MethodToolCallback
        tasksInfoTool.toolDefinition.name() == "tasksInfoTool"
        tasksInfoTool.toolDefinition.description() == TasksInfoTool.TOOL_DESCRIPTION
    }
}
