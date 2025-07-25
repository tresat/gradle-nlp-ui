@file:Suppress("UnstableApiUsage")

plugins {
    `java-gradle-plugin`
    id("library-conventions")
    id("test-conventions")
}

dependencies {
    implementation(project(":client"))
    implementation(project(":server"))

    testFixturesImplementation(gradleTestKit())
    testFixturesImplementation(testFixtures(project(":shared")))
    testFixturesImplementation(libs.commons.io)
}

testing {
    suites {
        named<JvmTestSuite>("test") {
            dependencies {
                implementation(testFixtures(project(":shared")))
            }
        }
        named<JvmTestSuite>("functionalTest") {
            dependencies {
                implementation(project(":shared"))
                implementation(testFixtures(project(":shared")))
            }
        }
    }
}

// TODO: A better fix for this
/*
 * Gradle's built-in logging implementation conflicts with Logback, provided by Spring, so exclude Logback.
 *
 * > LoggerFactory is not a Logback LoggerContext but Logback is on the classpath. Either remove Logback or the competing implementation
 *  (class org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext loaded from file:/Users/ttresansky/.gradle/wrapper/dists/gradle-8.14-bin/38aieal9i53h9rfe7vjup95b9/gradle-8.14/lib/gradle-logging-8.14.jar).
 * If you are using WebLogic you will need to add 'org.slf4j' to prefer-application-packages in WEB-INF/weblogic.xml: org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext
 */
configurations.all {
    exclude(module = "logback-classic")
}

gradlePlugin {
    testSourceSets.add(sourceSets["functionalTest"])

    plugins.create("gradleNlpUiPlugin") {
        id = "org.gradle.ai.nlp"
        implementationClass = "org.gradle.ai.nlp.plugin.GradleNlpUiPlugin"
    }
}

description = "This project provides a Gradle plugin for NLP Gradle build introspection."
