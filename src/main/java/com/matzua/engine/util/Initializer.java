package com.matzua.engine.util;

import java.util.function.UnaryOperator;

public interface Initializer<T> {
    default T defaultBuild() {
        throw Validation.newPlaceholderError();
    }
    default T init(UnaryOperator<T> initializer) {
        return initializer.apply(defaultBuild());
    }
    T build();
}
