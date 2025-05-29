package org.gradle.ai.nlp.client

import org.gradle.ai.nlp.exception.MissingRequiredPropertiesException
import spock.lang.Specification

class SpringMCPClientTest extends Specification {
    def "must provide all required properties to run client"() {
        when:
        SpringMCPClient.main(args as String[])

        then:
        def e = thrown(MissingRequiredPropertiesException)
        e.message.contains(MissingRequiredPropertiesException.requiredArgsMsg(SpringMCPClient.REQUIRED_PROPERTIES))
        e.message.contains(MissingRequiredPropertiesException.providedArgsMsg(args as String[]))

        where:
        args << [
                [],
                ["--spring.ai.mcp.client.bad=bad-arg"],
                ["--spring.config.name=sample"],
        ]
    }

    def "can provide additional properties when running client"() {
        expect:
        SpringMCPClient.verifyArgs([
                "--spring.ai.anthropic.api-key=test-key",
                "--spring.ai.mcp.client.other=something"
        ] as String[])
    }
}

