package org.gradle.ai.nlp.server.tools

import org.gradle.ai.nlp.server.AbstractMCPServerApplicationFunctionalTest
import org.gradle.ai.nlp.server.MCPServerApplication
import org.springframework.ai.tool.method.MethodToolCallback

class ProjectLocationsInfoToolFunctionalTest extends AbstractMCPServerApplicationFunctionalTest {
    def "server makes project locations tool available"() {
        when:
        def callbacks = context.getBean(ProjectLocationsInfoTool.TOOL_NAME).toolCallbacks

        then:
        callbacks.size() == 1
        def projectLocationsInfoTool = callbacks[0] as MethodToolCallback
        projectLocationsInfoTool.toolDefinition.name() == ProjectLocationsInfoTool.TOOL_NAME
        projectLocationsInfoTool.toolDefinition.description() == ProjectLocationsInfoTool.TOOL_DESCRIPTION
    }
}
