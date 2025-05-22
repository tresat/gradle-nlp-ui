@file:Suppress("UnstableApiUsage")

plugins {
    `java-gradle-plugin`
    groovy // For Spock tests
    `java-test-fixtures`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":test-weather-server"))

    api(platform(libs.spring.ai.bom))
    api(libs.spring.ai.starter.mcp.server)

    implementation(libs.guava)
    implementation(libs.spring.web)

    testFixturesImplementation(gradleTestKit())
    testFixturesImplementation(libs.spock.core)
}

// TODO: A better fix for this
/*
* > LoggerFactory is not a Logback LoggerContext but Logback is on the classpath. Either remove Logback or the competing implementation
*  (class org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext loaded from file:/Users/ttresansky/.gradle/wrapper/dists/gradle-8.14-bin/38aieal9i53h9rfe7vjup95b9/gradle-8.14/lib/gradle-logging-8.14.jar).
* If you are using WebLogic you will need to add 'org.slf4j' to prefer-application-packages in WEB-INF/weblogic.xml: org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext
*/
configurations.all {
    exclude(module = "spring-boot-starter-logging")
    exclude(module = "logback-classic")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useSpock()
        }

        val functionalTest by registering(JvmTestSuite::class) {
            useSpock()

            dependencies {
                implementation(testFixtures(project()))
            }

            targets {
                all {
                    // This test suite should run after the built-in test suite has run its tests
                    testTask.configure { shouldRunAfter(test) }
                }
            }
        }

        tasks.named<Task>("check") {
            // Include functionalTest as part of the check lifecycle
            dependsOn(functionalTest)
        }
    }
}

gradlePlugin {
    testSourceSets.add(sourceSets["functionalTest"])

    plugins.create("gradleNlpUiPlugin") {
        id = "org.gradle.ai.nlp"
        implementationClass = "org.gradle.ai.nlp.plugin.GradleNlpUiPlugin"
    }
}
