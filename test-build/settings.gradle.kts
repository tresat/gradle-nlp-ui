pluginManagement {
    includeBuild("../../gradle-nlp-ui")
}

plugins {
    id("com.gradle.develocity") version "3.17.5"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":core")

include(":ui")
project(":ui").projectDir = file("features/ui")

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
        termsOfUseAgree = "yes"
    }
}
