plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
    groovy // For Spock tests
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    api(project(":test-weather-server"))

    api(platform("org.springframework.ai:spring-ai-bom:1.0.0-M8"))
    api("org.springframework.ai:spring-ai-starter-mcp-server")
    implementation("org.springframework:spring-web:6.2.6")

    testImplementation(libs.spock.core)
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
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("2.1.20")
        }

        // Create a new test suite
        val functionalTest by registering(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("2.1.20")

            dependencies {
                // functionalTest test suite depends on the production code in tests
                implementation(project())
            }

            targets {
                all {
                    // This test suite should run after the built-in test suite has run its tests
                    testTask.configure { shouldRunAfter(test) } 
                }
            }
        }
    }
}

gradlePlugin {
    plugins.create("gradleNlpUiPlugin") {
        id = "org.gradle.ai.nlp"
        implementationClass = "org.gradle.ai.nlp.plugin.GradleNlpUiPlugin"
    }
}

gradlePlugin.testSourceSets.add(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}
