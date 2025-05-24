@file:Suppress("UnstableApiUsage")

plugins {
    `java-library`
    groovy // For Spock tests
    `java-test-fixtures`
}

testing {
    suites {
        withType<JvmTestSuite> {
            useSpock()
        }

        val test by getting(JvmTestSuite::class)
        val functionalTest by registering(JvmTestSuite::class) {
            dependencies {
                implementation(project())
            }

            targets {
                all {
                    testTask.configure { shouldRunAfter(test) }
                }
            }
        }

        tasks.named<Task>("check") {
            dependsOn(functionalTest)
        }
    }
}
