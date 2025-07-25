package org.gradle.ai.nlp.server

import org.springframework.web.client.RestTemplate

class ServerHealthFunctionalTest extends AbstractMCPServerApplicationFunctionalTest {
    def "can start server and ping server health with actuator"() {
        when:
        def restTemplate = new RestTemplate()
        def url = "$baseUrl/actuator/health"
        def response = restTemplate.getForEntity(url, String)

        then:
        response.statusCode.is2xxSuccessful()
        response.body != null
    }
}
