package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest
import org.gradle.ai.nlp.plugin.GradleNlpUiPlugin
import org.gradle.ai.nlp.plugin.service.MCPBuildService

class StartMCPTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "can run start server task"() {
        when:
        def result = succeeds(GradleNlpUiPlugin.START_MCP_SERVER_TASK_NAME)

        then:
        result.output.contains(MCPBuildService.SERVER_STARTUP_MESSAGE)
        result.output.contains(MCPBuildService.SERVER_SHUTDOWN_MESSAGE)
    }
}
