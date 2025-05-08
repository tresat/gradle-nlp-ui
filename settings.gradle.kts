dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            name = "Spring Milestones"
            url = uri("https://repo.spring.io/milestone")
        }
        maven {
            name = "Spring Snapshots"
            url = uri("https://repo.spring.io/snapshot")
        }
        maven {
            name = "Central Portal Snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
    }
}

rootProject.name = "gradle-nlp-ui"

include("plugin")
include("server")
