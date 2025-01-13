package com.matzua.engine.util;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Types {
    static <T, U> Consumer<T> cast(Consumer<U> consumer) {
        return Optional.of(consumer)
            .map(Consumer.class::cast)
            .map(Function.<Consumer<T>>identity()::apply)
            .orElseThrow(Validation::newPlaceholderError);
    }
    static <T, U, R> BiFunction<T, U, R> cast(BiFunction<T, U, R> biFunction) {
        return Optional.of(biFunction)
            .map(BiFunction.class::cast)
            .map(Function.<BiFunction<T, U, R>>identity()::apply)
            .orElseThrow(Validation::newPlaceholderError);
    }
    static <T, U> Class<T> cast(Class<U> clazz) {
        return Optional.of(clazz)
            .map(Class.class::cast)
            .map(Function.<Class<T>>identity()::apply)
            .orElseThrow(Validation::newPlaceholderError);
    }
}
