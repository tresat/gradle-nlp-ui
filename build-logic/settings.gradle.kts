@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        mavenCentral()

        repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    }
}

include("conventions")

rootProject.name = "build-logic"