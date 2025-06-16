package org.gradle.ai.nlp.server

import org.gradle.ai.nlp.exception.MissingRequiredPropertiesException
import spock.lang.Specification

class MCPServerApplicationTest extends Specification {
    def "must provide all required properties to run server"() {
        when:
        MCPServerApplication.main(args as String[])

        then:
        def e = thrown(MissingRequiredPropertiesException)
        e.message.contains(MissingRequiredPropertiesException.requiredArgsMsg(MCPServerApplication.REQUIRED_PROPERTIES))
        e.message.contains(MissingRequiredPropertiesException.providedArgsMsg(args as String[]))

        where:
        args << [
            [],
            ["--${MCPServerApplication.SERVER_PORT_PROPERTY}=8080"],
            ["--${MCPServerApplication.TASKS_REPORT_FILE_PROPERTY}=build/reports/tasks-report.txt"],
            ["--${MCPServerApplication.SERVER_PORT_PROPERTY}=8080", "--${MCPServerApplication.TASKS_REPORT_FILE_PROPERTY}=build/reports/tasks-report.txt"]
        ]
    }

    def "can provide additional properties when running server"() {
        expect:
        MCPServerApplication.verifyArgs([
                "--${MCPServerApplication.SERVER_PORT_PROPERTY}=8080",
                "--${MCPServerApplication.ANTHROPIC_API_KEY_PROPERTY}=test-key",
                "--${MCPServerApplication.LOG_FILE_PROPERTY}=build/logs/mcp-server.log",
                "--${MCPServerApplication.TASKS_REPORT_FILE_PROPERTY}=build/reports/tasks-report.txt",
                "--${MCPServerApplication.GRADLE_FILES_REPORT_FILE_PROPERTY}=build/reports/gradle-files-report.txt",
                "--spring.ai.mcp.server.other=something",
        ] as String[])
    }
}
