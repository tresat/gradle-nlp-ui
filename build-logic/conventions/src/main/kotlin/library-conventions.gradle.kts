@file:Suppress("UnstableApiUsage")

// Access the version catalog in the included project
val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

plugins {
    `java-library`
}

dependencies {
    api(platform(libs.findLibrary("spring.ai.bom").get()))

    api(libs.findLibrary("mcp.sdk").get())
    api(libs.findLibrary("guava").get())

    if (project.name != "shared") {
        implementation(project(":shared"))
    }
}
