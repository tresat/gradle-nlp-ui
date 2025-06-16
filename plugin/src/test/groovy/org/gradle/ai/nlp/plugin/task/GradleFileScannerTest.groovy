package org.gradle.ai.nlp.plugin.task

import org.gradle.ai.nlp.test.TestUtil
import org.gradle.ai.nlp.util.Util
import spock.lang.Specification
import spock.lang.TempDir

class GradleFileScannerTest extends Specification {
    private scanner = new GradleFileScanner()

    @TempDir
    File tempDir

    def "collectGradleFiles collects build files"() {
        given:
        loadTestDir("sample1")

        when:
        def result = scanner.collectGradleFiles(tempDir)

        then:
        result*.relativePathFrom(tempDir) == [
            "settings.gradle",
            "something/other/settings.gradle",
            "sub1/build.gradle",
            "build-logic/settings.gradle.kts",
            "sub2/build.gradle.kts"
        ]
    }

    def "collectGradleFiles collects build files when includedBuilds recursively include builds"() {
        given:
        loadTestDir("sample2")

        when:
        def result = scanner.collectGradleFiles(new File(tempDir, "root"))

        then:
        result*.relativePathFrom(tempDir) == [
            "root/settings.gradle",
            "root/sub1/build.gradle",
            "root/sub2/build.gradle.kts",
            "build-logic/settings.gradle.kts",
            "other/settings.gradle",
            "other/sub1/build.gradle",
        ]
    }

    private void loadTestDir(String dirName) {
        def relativePath = "org/gradle/ai/nlp/plugin/task/${dirName}"
        TestUtil.copyResourceDir(relativePath, tempDir)
    }

    def setup() {
        // TODO: Automatically load this extension method for test and prod code
        File.metaClass.relativePathFrom = { File baseDir ->
            Util.relativePathFrom(delegate as File, baseDir)
        }
    }
}
