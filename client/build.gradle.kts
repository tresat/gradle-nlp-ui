plugins {
    id("library-conventions")
    id("test-conventions")
    //id("anthropic-model-conventions")
}

dependencies {
    // TODO: These deps should be trimmed, and use the version catalog, but it was a pain to get this working

    implementation(libs.spring.ai.starter.mcp.client.webflux)
    // https://mvnrepository.com/artifact/org.springframework.ai/spring-ai-model
    implementation("org.springframework.ai:spring-ai-starter-mcp-client-webflux:1.0.0")
    implementation("org.springframework.ai:spring-ai-mcp:1.0.0")

    implementation("org.springframework.ai:spring-ai-model")
    implementation("org.springframework.ai:spring-ai-starter-model-anthropic")

    api("org.springframework.ai:spring-ai-client-chat")
    implementation("org.springframework.ai:spring-ai-autoconfigure-model-chat-client")

    implementation("org.springframework.boot:spring-boot-starter-webflux:3.5.0")

    implementation("io.netty:netty-resolver-dns-native-macos:4.2.1.Final:osx-aarch_64") {
        /*
         * This dependency is required for macOS ARM64 (Apple Silicon) to resolve DNS.
         * The 'osx-aarch_64' classifier ensures that the correct native library is used.
         * If you are not on macOS ARM64, you can remove this dependency.
         *
         * Otherwise, adding it prevents an error:
         * Unable to load io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS. Check whether you have a dependency on 'io.netty:netty-resolver-dns-native-macos'. Use DEBUG level to see the full stack: java.lang.UnsatisfiedLinkError: failed to load the required native library
         */
        because("This is a workaround for the issue with Netty DNS resolution on macOS ARM64.")
    }
}

testing {
    suites {
        named<JvmTestSuite>("functionalTest").configure {
            targets.all {
                testTask.configure {
                    dependsOn(project(":server").tasks.named("assemble"))
                }
            }
        }
    }
}

description = """This project provides an MCP client built with Spring AI.  This project is ignorant of Gradle, 
and can be run externally to a build to connect to an MCP server."""
