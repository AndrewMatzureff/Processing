package com.matzua.engine.util;

import java.util.Arrays;
import java.util.Objects;

import static com.matzua.engine.util.Fun.*;

public interface Validation {
    static void requireNonNull(Object... objects) {
        Arrays.stream(objects).forEach(Objects::requireNonNull);
    }

    static void requireNonNull(String message, Object... objects) {
        Arrays.stream(objects).forEach(fu(message, Objects::requireNonNull)::apply);
    }
//    static void requireNull(Object... objects) {
//        requireNull("TODO: update this message!", objects);
//    }
//
//    static void requireNull(String message, Object... objects) {
//        if (Arrays.stream(objects).anyMatch(Objects::nonNull)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
}
