@file:Suppress("UnstableApiUsage")

import gradle.kotlin.dsl.accessors._de6aa903e3717a2c32516b1c60673d8d.api


// Access the version catalog in the included project
val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

plugins {
    `java-library`
}

dependencies {
    api(platform(libs.findLibrary("spring.ai.bom").get()))

    implementation(platform(libs.findLibrary("spring.ai.bom").get()))
    implementation(libs.findLibrary("mcp.sdk").get())
    implementation(libs.findLibrary("guava").get())
}
