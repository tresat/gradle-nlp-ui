import org.gradle.ai.nlp.build.task.CheckAnthropicApiKeyTask

// Access the version catalog in the included project
val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

plugins {
    id("library-conventions")
}

dependencies {
    implementation(libs.findLibrary("spring.ai.starter.model.anthropic").get())
}

tasks.register("checkAnthropicApiKey", CheckAnthropicApiKeyTask::class) {
    group = "Verification"
    description = "Ensures ANTHROPIC_API_KEY is set as a project property or environment variable."
}

//tasks.named("check") {
//    dependsOn("checkAnthropicApiKey")
//}
