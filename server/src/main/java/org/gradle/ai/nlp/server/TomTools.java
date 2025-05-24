package org.gradle.ai.nlp.server;

import org.springframework.ai.tool.annotation.Tool;

public class TomTools {
    @Tool(description = "Tom's tools")
    public int favoriteNumber() {
        return 42;
    }
}
