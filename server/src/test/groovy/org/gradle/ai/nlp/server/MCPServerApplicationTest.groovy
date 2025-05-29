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
            ["--server.port=8080"],
            ["--org.gradle.ai.nlp.server.tasks.report.file=build/reports/tasks-report.txt"],
            ["--server.port=8080", "--org.gradle.ai.nlp.server.tasks.report.file=build/reports/tasks-report.txt"]
        ]
    }

    def "can provide additional properties when running server"() {
        expect:
        MCPServerApplication.verifyArgs([
                "--server.port=8080",
                "--org.gradle.ai.nlp.server.tasks.report.file=build/reports/tasks-report.txt",
                "--logging.file.name=build/logs/mcp-server.log",
                "--spring.ai.anthropic.api-key=test-key",
                "--spring.ai.mcp.server.other=something",
        ] as String[])
    }
}
