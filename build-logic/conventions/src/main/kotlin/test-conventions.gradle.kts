@file:Suppress("UnstableApiUsage")

// Access the version catalog in the included project
val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

plugins {
    id("library-conventions")
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
                implementation(libs.findLibrary("spring.boot.starter.test").get())
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