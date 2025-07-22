package org.gradle.ai.nlp.client;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration
public class ReactorConfig {
    @PostConstruct
    public void configureReactorHooks() {
        Hooks.onErrorDropped(error -> {
            // Handle the dropped error
            System.err.println("onErrorDropped handler invoked due to error: " + error.getMessage());
        });
    }
}
