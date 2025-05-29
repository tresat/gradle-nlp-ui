package org.gradle.ai.nlp.exception;

import java.util.List;

public final class MissingRequiredPropertiesException extends RuntimeException {
    public MissingRequiredPropertiesException(List<String> requiredArgs, String[] args) {
        super(requiredArgsMsg(requiredArgs) + providedArgsMsg(args));
    }

    public static String requiredArgsMsg(List<String> requiredArgs) {
        return "The following properties are all required:\n\t" + String.join("\n\t", requiredArgs) + "\n";
    }

    public static String providedArgsMsg(String[] args) {
        return "Properties provided:\n\t" + String.join("\n\t", args) + "\n";
    }
}