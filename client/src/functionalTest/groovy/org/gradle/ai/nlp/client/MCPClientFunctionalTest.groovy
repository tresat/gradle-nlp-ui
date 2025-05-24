package org.gradle.ai.nlp.client

import org.gradle.ai.nlp.server.McpServerApplication
import spock.lang.Specification

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.SpringApplication;

class MCPClientFunctionalTest extends Specification {
    private ConfigurableApplicationContext serverContext
//    private MCPClient client

    def setup() {
        //System.setProperty("spring.config.location", "file:/Users/ttresansky/Projects/ai/gradle-nlp-ui/client/src/functionalTest/resources/server.properties")
        SpringApplication app = new SpringApplication(McpServerApplication.class)
        app.setDefaultProperties(["spring.config.location": "file:/Users/ttresansky/Projects/ai/gradle-nlp-ui/client/src/functionalTest/resources/server.properties"])
//                                  "spring.ai.mcp.server.enabled": "true",
//                                  "spring.groovy.template.check-template-location": "false"])
        serverContext = app.run()
    }

    def cleanup() {
        serverContext.close()
    }

    def "can start server"() {
        expect:
        serverContext != null
        serverContext.isActive()
    }

//    def "can connect to server"() {
//        when:
//        client = new MCPClient()
//
//        then:
//        client.isConnected()
//    }

//        given: "A query string"
//        String query = "What is AI?"
//
//        when: "The client sends the query to the server"
//        String response = client.query(query)
//
//        then: "The response is as expected"
//        response == "42" // Simulated response
}
