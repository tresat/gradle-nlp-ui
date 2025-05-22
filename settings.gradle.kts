@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        maven {
            name = "Central Portal Snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
        maven {
            name = "Spring Milestones"
            url = uri("https://repo.spring.io/milestone")
        }
        maven {
            name = "Spring Snapshots"
            url = uri("https://repo.spring.io/snapshot")
        }
        mavenCentral()

        repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    }
}

include("client")
include("server")
include("plugin")

// Test projects
include("test-weather-server")
include("test-weather-java-client")
include("test-java-spring-chat-client")

rootProject.name = "gradle-nlp-ui"
