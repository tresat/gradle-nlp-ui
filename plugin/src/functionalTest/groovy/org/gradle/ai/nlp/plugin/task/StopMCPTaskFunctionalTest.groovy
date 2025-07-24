package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.plugin.AbsractGradleNlpUiPluginFunctionalTest
import org.gradle.ai.nlp.plugin.service.MCPServerService

class StopMCPTaskFunctionalTest extends AbsractGradleNlpUiPluginFunctionalTest {
    def "can run stop server task"() {
        when:
        def result = succeeds(StartMCPTask.NAME, StopMCPTask.NAME)

        then:
        result.output.contains(MCPServerService.SERVER_STARTUP_MESSAGE)
        result.output.contains(MCPServerService.SERVER_SHUTDOWN_MESSAGE)
    }
}