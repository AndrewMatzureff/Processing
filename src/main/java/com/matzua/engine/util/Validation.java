package com.matzua.engine.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.matzua.engine.util.Fun.*;

public interface Validation {
    static void requireNonNull(Object... objects) {
        Arrays.stream(objects).forEach(Objects::requireNonNull);
    }

    static void requireNonNull(String message, Object... objects) {
        Arrays.stream(objects).forEach(fu(message, Objects::requireNonNull)::apply);
    }

    static RuntimeException newPlaceholderError() {
        return new RuntimeException("TODO: Update this Exception!!!");
    }

    static Supplier<RuntimeException> newPlaceholderError(String message) {
        return () -> new RuntimeException(message);
    }

    static Consumer<Runnable> ifAllPresent(Object...dependencies) {
        if (Arrays.stream(dependencies)
            .allMatch(Objects::nonNull)) {
            return Runnable::run;
        }
        return runnable -> {};
    }
}
