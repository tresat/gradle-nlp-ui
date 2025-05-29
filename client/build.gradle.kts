@file:Suppress("UnstableApiUsage")

plugins {
    id("library-conventions")
    id("test-conventions")
    id("anthropic-model-conventions")
}

dependencies {
    api(libs.spring.ai.client.chat)
    api(libs.spring.ai.starter.mcp.client.webflux)

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

    testFixturesImplementation(testFixtures(project(":shared")))
}

testing {
    suites {
        named<JvmTestSuite>("functionalTest").configure {
            dependencies {
                implementation(testFixtures(project(":shared")))
            }

            targets.all {
                testTask.configure {
                    // Ensure the server jar is re-built every time the client tests run
                    dependsOn(project(":server").tasks.named("assemble"))
                }
            }
        }
    }
}

description = """This project provides an MCP client built with Spring AI.  This project is ignorant of Gradle, 
and can be run externally to a build to connect to an MCP server."""
