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
}

// TODO: A better fix for this
/*
* > LoggerFactory is not a Logback LoggerContext but Logback is on the classpath. Either remove Logback or the competing implementation
*  (class org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext loaded from file:/Users/ttresansky/.gradle/wrapper/dists/gradle-8.14-bin/38aieal9i53h9rfe7vjup95b9/gradle-8.14/lib/gradle-logging-8.14.jar).
* If you are using WebLogic you will need to add 'org.slf4j' to prefer-application-packages in WEB-INF/weblogic.xml: org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext
*/
configurations.all {
    exclude(module = "logback-classic")
}
