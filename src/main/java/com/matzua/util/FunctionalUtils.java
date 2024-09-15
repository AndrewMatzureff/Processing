package com.matzua.util;

import java.util.Map;
import java.util.function.*;

public interface FunctionalUtils {
    interface BindableBiConsumerSupplier<T, U> extends Supplier<BiConsumer<T, U>> {
        default Consumer<T> bind(U u) {
            return t -> get().accept(t, u);
        }

        default Runnable bind(T t, U u) {
            return () -> get().accept(t, u);
        }
    }

    interface BindableBiFunctionSupplier<T, U, R> extends Supplier<BiFunction<T, U, R>> {
        default Function<T, R> bind(U u) {
            return t -> get().apply(t, u);
        }
        default Supplier<R> bind(T t, U u) {
            return () -> get().apply(t, u);
        }

    }

    interface BindableBiPredicateSupplier<T, U> extends Supplier<BiPredicate<T, U>> {
        default Predicate<T> bind(U u) {
            return t -> get().test(t, u);
        }
        default Supplier<Boolean> bind(T t, U u) {
            return () -> get().test(t, u);
        }

    }

    static <T, U> BindableBiConsumerSupplier<T, U> biConsumer(BiConsumer<T, U> biConsumer) {
        return () -> biConsumer;
    }

    static <T, U, R> BindableBiFunctionSupplier<T, U, R> biFunction(BiFunction<T, U, R> biFunction) {
        return () -> biFunction;
    }

    static <T, U> BindableBiPredicateSupplier<T, U> biPredicate(BiPredicate<T, U> biPredicate) {
        return () -> biPredicate;
    }

    static <T> Function<T, T> identity(Class<T> clazz) {
        return Function.identity();
    }

    static <T, U> Function<T, U> remap(U value) {
        return arg -> value;
    }
}
