@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("com.gradle.develocity") version "3.17.5"
}

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

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
        termsOfUseAgree = "yes"
    }
}

include("client")
include("server")
include("shared")
include("plugin")

// Test projects
include("test-weather-server")

rootProject.name = "gradle-nlp-ui"
