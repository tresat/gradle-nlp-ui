package org.gradle

import org.gradle.ai.nlp.server.McpServerApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class Test4 extends Specification {
    private ConfigurableApplicationContext context

    def setup() {
        context = McpServerApplication.run()
    }

    def cleanup() {
        if (context != null && context.isActive()) {
            context.close()
        }
    }

    def "manual server startup"() {
        when:
        def restTemplate = new RestTemplate()
        def url = "http://localhost:8080/actuator/health"
        def response = restTemplate.getForEntity(url, String)

        then:
        response.statusCode.is2xxSuccessful()
        response.body != null
    }
}
