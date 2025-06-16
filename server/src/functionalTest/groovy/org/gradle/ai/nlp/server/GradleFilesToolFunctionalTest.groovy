package org.gradle.ai.nlp.server

import org.springframework.ai.tool.method.MethodToolCallback

class GradleFilesToolFunctionalTest extends AbstractMCPServerApplicationFunctionalTest {
    def "server makes gradle files tool available"() {
        when:
        def callbacks = context.getBean(MCPServerApplication.GRADLE_FILES_TOOL_NAME).toolCallbacks

        then:
        callbacks.size() == 1
        def tasksInfoTool = callbacks[0] as MethodToolCallback
        tasksInfoTool.toolDefinition.name() == "gradleFilesTool"
        tasksInfoTool.toolDefinition.description() == GradleFilesTool.TOOL_DESCRIPTION
    }
}
