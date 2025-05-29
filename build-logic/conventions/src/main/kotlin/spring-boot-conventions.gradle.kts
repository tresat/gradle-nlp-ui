@file:Suppress("UnstableApiUsage")

plugins {
    id("library-conventions")
    id("test-conventions")
    id("org.springframework.boot")
}

testing {
    suites {
        named<JvmTestSuite>("functionalTest").configure {
            targets.all {
                testTask.configure {
                    dependsOn(tasks.named("bootJar"))
                }
            }
        }
    }
}
