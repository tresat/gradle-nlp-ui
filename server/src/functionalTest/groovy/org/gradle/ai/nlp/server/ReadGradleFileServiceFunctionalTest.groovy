package org.gradle.ai.nlp.server

class ReadGradleFileServiceFunctionalTest extends AbstractMCPServerApplicationFunctionalTest {
    def "server makes gradle files contents service available"() {
        given:
        def file = new File(tempDir, "build.gradle")
        def contents = """
            name = 'my-test-project'
            version = '1.0.0'
            description = 'Is this file findable?'
        """
        file.write(contents)

        when:
        def service = context.getBean(MCPServerApplication.GRADLE_FILE_CONTENTS_TOOL_NAME)

        then:
        service.class == ReadGradleFileService.class
        def typedService = service as ReadGradleFileService
        def result = typedService.apply(file.absolutePath)
        result == contents
    }
}
