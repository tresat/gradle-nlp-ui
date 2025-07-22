package org.gradle.ai.nlp.exception;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class MissingRequiredPropertiesException extends RuntimeException {
    public MissingRequiredPropertiesException(List<String> requiredArgs, String[] args) {
        super(requiredArgsMsg(requiredArgs) + providedArgsMsg(args) + missingPropertiesMsg(requiredArgs, args));
    }

    public static String requiredArgsMsg(List<String> requiredArgs) {
        return "The following properties are all required:\n\t" + String.join("\n\t", requiredArgs.stream().sorted(Comparator.naturalOrder()).toList()) + "\n";
    }

    public static String providedArgsMsg(String[] args) {
        return "Properties provided:\n\t" + String.join("\n\t", Arrays.stream(args).sorted(Comparator.naturalOrder()).toList()) + "\n";
    }

    /**
     * Returns a message listing the missing properties from the provided arguments.
     *
     * @param requiredArgs List of required property names.
     * @param args Array of provided arguments.
     * @return A formatted string indicating which required properties are missing.
     */
    public static String missingPropertiesMsg(List<String> requiredArgs, String[] args) {
        List<String> providedArgsList = Arrays.asList(args);
        String missingProperties = requiredArgs.stream()
                .filter(requiredArg -> !providedArgsList.contains(requiredArg))
                .sorted(Comparator.naturalOrder())
                .toList()
                .toString();

        return "Missing properties:\n\t" + missingProperties + "\n";
    }
}
