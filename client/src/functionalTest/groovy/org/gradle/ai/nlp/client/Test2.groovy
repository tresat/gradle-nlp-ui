package org.gradle.ai.nlp.client

import org.gradle.ai.nlp.server.McpServerApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class Test2 extends Specification {
    @Autowired
    McpServerApplication mcpServerApplication

    void setup() {
        SpringApplication.run(mcpServerApplication)
    }

    @LocalServerPort
    int port

    def "server responds to ping"() {
        given:
        def restTemplate = new RestTemplate()
        def url = "http://localhost:${port}/actuator/health"

        when:
        def response = restTemplate.getForEntity(url, String)

        then:
        response.statusCode.is2xxSuccessful()
        response.body != null
    }
}

