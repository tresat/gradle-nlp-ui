plugins {
    id("library-conventions")
    id("test-conventions")
}

description = """Shared utilities for working with Spring and MCP"""

dependencies {
    testFixturesImplementation(libs.javax.json.api)
    testFixturesImplementation(libs.javax.json)
}
