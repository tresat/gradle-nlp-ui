package org.gradle.ai.nlp.client


import spock.lang.Specification

class MCPClientFunctionalTest extends Specification {
    // TODO: obviously bad
    static final String PATH_TO_SERVER_JAR = "/Users/ttresansky/Projects/ai/gradle-nlp-ui/server/build/libs/server-0.1.0-SNAPSHOT.jar"

    static serverProcess

    def setupSpec() {
        // Start the server JAR as a background process
        def process = ["java", "-jar", PATH_TO_SERVER_JAR].execute()
        process.consumeProcessOutput(System.out, System.err)
        // Store the process for cleanup
        serverProcess = process

        // TODO: find a better way to wait for the server to start
        Thread.sleep(5000)
    }

    def cleanupSpec() {
        if (serverProcess) {
            serverProcess.destroy()
            serverProcess.waitForOrKill(5000)
            serverProcess = null
            println "Server process terminated."
        }
    }

    def "client can connect to server"() {
        given:
        MCPClient client = new MCPClient()

        when:
        client.connect()

        then:
        client.isConnected()
    }
}
