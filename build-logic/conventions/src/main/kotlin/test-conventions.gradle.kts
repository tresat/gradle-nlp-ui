@file:Suppress("UnstableApiUsage")

// Access the version catalog in the included project
val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

plugins {
    id("library-conventions")
    groovy // For Spock tests
    `java-test-fixtures`
}

dependencies {
    testFixturesImplementation(libs.findLibrary("spock.core").get())
    if (project.name != "shared") {
        testFixturesImplementation(project(":shared"))
    }
}

testing {
    suites {
        withType<JvmTestSuite> {
            useSpock()
        }

        val test by getting(JvmTestSuite::class) {
            dependencies {
                implementation(libs.findLibrary("mockito.core").get())
                implementation(libs.findLibrary("kotlin.stdlib").get()) {
                    because("""
                        Required for debugging tests, otherwise:
                            Exception in thread "main" Native frames: (J=compiled Java code, j=interpreted, Vv=VM code, C=native code)
                            java.lang.NoClassDefFoundError: kotlin/Result
                    """.trimMargin())
                }
            }
        }
        val functionalTest by registering(JvmTestSuite::class) {
            dependencies {
                implementation(project())
                implementation(testFixtures(project()))
                implementation(libs.findLibrary("spring.boot.starter.test").get())
                implementation(libs.findLibrary("kotlin.stdlib").get()) {
                    because("""
                        Required for debugging tests, otherwise:
                            Exception in thread "main" Native frames: (J=compiled Java code, j=interpreted, Vv=VM code, C=native code)
                            java.lang.NoClassDefFoundError: kotlin/Result
                    """.trimMargin())
                }
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

