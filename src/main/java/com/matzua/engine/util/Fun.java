package com.matzua.engine.util;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.*;
import java.util.regex.Pattern;

public interface Fun {

    // ================================================================================== // functional interfaces \\\\

    interface SerializableLambda extends Serializable {
        record MethodReference (
            String implClass,
            int implMethodKind,
            String implMethodName,
            String implMethodSignature,
            String instantiatedMethodType
        ) {
            @Override
            public String toString() {
                return "{%s}".formatted(Map.of(
                    "implClass",                implClass,
                    "implMethodKind",           implMethodKind,
                    "implMethodName",           implMethodName,
                    "implMethodSignature",      implMethodSignature,
                    "instantiatedMethodType",   instantiatedMethodType)
                    .entrySet()
                    .stream()
                    .map(e -> "\"%s\":\"%s\"".formatted(e.getKey(), e.getValue()))
                    .sorted()
                    .reduce((a, b) -> a + "," + b)
                    .orElse("")
                );
            }

            public String toReferenceName() {
                final int i = Math.max(implClass.lastIndexOf('/'), implClass.lastIndexOf('$'));
                return "%s::%s".formatted(implClass.substring(i + 1), implMethodName);
            }

//            @Override
//            public boolean equals(final Object other) {
//                return Optional.of(other)
//                    .filter(o -> o.getClass().equals(this.getClass()))
//                    .map(this.getClass()::cast)
//                    .map(MethodReference::serializedLambda)
//                    .filter(getters()
//                        .map(this::meq)
//                        .reduce(Predicate::and)
//                        .orElseThrow(() -> new RuntimeException("TODO: update this message!!!")))
//                    .isPresent();
//            }
//
//            @Override
//            public int hashCode() {
//                return Objects.hash(getters()
//                    .map(fu(this.serializedLambda(), Function::apply))
//                    .toArray());
//            }
//
//            private Map<String, Function<SerializedLambda, ?>> properties() {
//                return Map.of(
//                    "implClass",                SerializedLambda::getImplClass,
//                    "implMethodKind",           SerializedLambda::getImplMethodKind,
//                    "implMethodName",           SerializedLambda::getImplMethodName,
//                    "implMethodSignature",      SerializedLambda::getImplMethodSignature,
//                    "instantiatedMethodType",   SerializedLambda::getInstantiatedMethodType
//                );
//            }
//
//            private Stream<Function<SerializedLambda, ?>> getters() {
//                return properties().values().stream();
//            }
//
//            private <T> Predicate<SerializedLambda> meq(Function<SerializedLambda, T> getter) {
//                return other -> getter.apply(other).equals(getter.apply(this.serializedLambda()));
//            }
        }
        Pattern pattern_InstantiatedMethodType = Pattern.compile("");
        private SerializedLambda toSerializedLambda() {
            try {
                final Method writeReplace = this.getClass().getDeclaredMethod("writeReplace");
                writeReplace.setAccessible(true);
                return (SerializedLambda) writeReplace.invoke(this);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException("TODO: update this message!!!", e);
            }
        }

        default MethodReference toMethodReference(final Class<?> implClass) {
            final SerializedLambda sl = this.toSerializedLambda();

            if (implClass != null && !implClass.getName().replace('.', '/').equals(sl.getImplClass())) {
                throw Validation.newPlaceholderError();
            }

            return new MethodReference(
                sl.getImplClass(),
                sl.getImplMethodKind(),
                sl.getImplMethodName(),
                sl.getImplMethodSignature(),
                sl.getInstantiatedMethodType()
            );
        }

//        default String toString(final String separator) {
//            final SerializedLambda serializedLambda = this.toSerializedLambda();
//            return "%s%s%s".formatted(
//                serializedLambda.getImplClass(),
//                Optional.ofNullable(separator).orElse("/"),
//                serializedLambda.getImplMethodName());
//        }

//        default boolean isMethodReference() {
////            boolean b = ((Predicate<Object>)((Object o) -> true)).and(Objects::isNull).
//            // implementation=invokeVirtual com/matzua/engine/app/config/Config.getWindowInfoTitle:()Ljava/lang/String;
//            // instantiatedMethodType=(Lcom/matzua/engine/app/config/Config;)Ljava/lang/String;
//            // ...
//            // implementation=invokeStatic com/matzua/engine/app/config/Config.testWIT:(Lcom/matzua/engine/app/config/Config;)Ljava/lang/String;
//            // instantiatedMethodType=(Lcom/matzua/engine/app/config/Config;)Ljava/lang/String;
//            // ...
//            // implementation=invokeInterface java/util/function/Function.apply:(Ljava/lang/Object;)Ljava/lang/Object;
//            // instantiatedMethodType=(Lcom/matzua/engine/app/config/Config;)Ljava/lang/String;
//            final SerializedLambda serializedLambda = this.toSerializedLambda();
//            final String implClass = serializedLambda.getImplClass();
//
//            return true;//serializedLambda.get
//        }
    }

    @FunctionalInterface
    interface SerializableQuadConsumer<T, U, V, W> extends SerializableLambda, BiConsumer<Map.Entry<T, U>, Map.Entry<V, W>> {
        void accept(T t, U u, V v, W w);
        @Override
        default void accept(Map.Entry<T, U> tuEntry, Map.Entry<V, W> vwEntry) {
            accept(tuEntry.getKey(), tuEntry.getValue(), vwEntry.getKey(), vwEntry.getValue());
        }
        static <T, U, V, W> SerializableQuadConsumer<T, U, V, W> of(SerializableQuadConsumer<T, U, V, W> reference) {
            return reference;
        }
    }

    @FunctionalInterface
    interface SerializableTriConsumer<T, U, V> extends SerializableLambda, BiConsumer<T, Map.Entry<U, V>> {
        void accept(T t, U u, V v);
        @Override
        default void accept(T t, Map.Entry<U, V> uvEntry) {
            accept(t, uvEntry.getKey(), uvEntry.getValue());
        }
        static <T, U, V> SerializableTriConsumer<T, U, V> of(SerializableTriConsumer<T, U, V> reference) {
            return reference;
        }
    }

    @FunctionalInterface
    interface SerializableBiConsumer<T, U> extends SerializableLambda, BiConsumer<T, U> {}

    @FunctionalInterface
    interface SerializableFunction<T, R> extends SerializableLambda, Function<T, R> {}

    // =============================================================================================== // wrappers \\\\

    // NOTE: instead of the format "<wrapper>(<arg>, <lambda>)" try exploring "<const>(<arg>).<wrapper>(<lambda>)"...
    /*
            Fun.___(     ).from(Example::trifunction);  // 3 -> 3
            Fun.__$(    z).from(Example::trifunction);  // 3 -> 2
            Fun._$_(  y  ).from(Example::trifunction);  // 3 -> 2
            Fun._$$(  y,z).from(Example::trifunction);  // 3 -> 1
            Fun.$__(x    ).from(Example::trifunction);  // 3 -> 2
            Fun.$_$(x,  z).from(Example::trifunction);  // 3 -> 1
            Fun.$$_(x,y  ).from(Example::trifunction);  // 3 -> 1
            Fun.$$$(x,y,z).from(Example::trifunction);  // 3 -> 0
     */

    //// identities \\ ============================================================================================= \\

    /**
     * Identity helper which returns the provided {@code BiConsumer}.
     * @param bc {@link BiConsumer}
     * @return {@code bc}
     * @param T {@code T} in {@code BiConsumer<T, U>}
     * @param U {@code U} in {@code BiConsumer<T, U>}
     */
    static <T, U> BiConsumer<T, U> bc(BiConsumer<T, U> bc) {return bc;} //          2 -> 0

    /**
     * Identity helper which returns the provided {@code Consumer}.
     * @param c {@link Consumer}
     * @return {@code c}
     * @param T {@code T} in {@code Consumer<T>}
     */
    static <T> Consumer<T> c(Consumer<T> c) {return c;} //                          1 -> 0

    /**
     * Identity helper which returns the provided {@code Runnable}.
     * @param r {@link Runnable}
     * @return {@code r}
     */
    static Runnable r(Runnable r) {return r;} //                                    0 -> 0

    /**
     * Identity helper which returns the provided {@code Supplier}.
     * @param s {@link Supplier}
     * @return {@code s}
     * @param T {@code T} in {@code Supplier<T>}
     */
    static <T> Supplier<T> s(Supplier<T> s) {return s;} //                          0 -> 1

    /**
     * Identity helper which returns the provided {@code Function}.
     * @param f {@link Function}
     * @return {@code f}
     * @param T {@code T} in {@code Function<T, R>}
     * @param R {@code R} in {@code Function<T, R>}
     */
    static <T, R> Function<T, R> f(Function<T, R> f) {return f;} //                 1 -> 1

    /**
     * Identity helper which returns the provided {@code BiFunction}.
     * @param bf {@link BiFunction}
     * @return {@code bf}
     * @param T {@code T} in {@code BiFunction<T, U, R>}
     * @param U {@code U} in {@code BiFunction<T, U, R>}
     * @param R {@code R} in {@code BiFunction<T, U, R>}
     */
    static <T, U, R> BiFunction<T, U, R> bf(BiFunction<T, U, R> bf) {return bf;} // 2 -> 1

    //// biconsumers \\ ============================================================================================ \\

    //// consumers \\ ============================================================================================== \\

    //// functions \\ ============================================================================================== \\

    /**
     * Unification helper which grounds the 2nd parameter of the provided {@code BiFunction} using the provided value
     * in order to produce an arity-1 {@code Function}.
     * @param u {@link U} or the 2nd parameter of {@code bf} to ground
     * @param bf {@link BiFunction}
     * @return a {@link Function} which applies {@code bf} to some unknown {@link T} and the given {@link U}.
     * @param T {@link T} in {@code BiFunction<T, U, R>}
     * @param U {@link U} in {@code BiFunction<T, U, R>}
     * @param R {@link R} in {@code BiFunction<T, U, R>}
     */
    static <T, U, R> Function<T, R> fu(U u, BiFunction<T, U, R> bf) {return t -> bf.apply(t, u);}

    //// bifunctions \\ ============================================================================================ \\

    static <T, U, R1, R2> BiFunction<T, U, R2> bf(R2 r, BiFunction<T, U, R1> bf) {return bf.andThen(r1 -> r);}

    //// suppliers \\ ============================================================================================== \\

    /**
     * Unification helper which grounds all elements of the array parameter of the provided {@code Function} using the
     * provided varargs in order to produce a {@code Supplier}.
     * @param f {@link Function}
     * @param t {@link T} or the array parameter of {@code f} to ground the elements of
     * @return a {@link Supplier} which applies {@code f} to the given {@link T} elements.
     * @param T {@link T} in {@code Function<T, R>}
     * @param R {@link R} in {@code Function<T, R>}
     */
    @SafeVarargs
    static <T, R> Supplier<R> s(Function<T[], R> f, T... t) {return () -> f.apply(t);}

    /**
     * Unification helper which grounds the sole parameter of the provided {@code Function} using the provided value in
     * order to produce a {@code Supplier}.
     * @param f {@link Function}
     * @param t {@link T} or the sole parameter of {@code f} to ground
     * @return a {@link Supplier} which applies {@code f} to the given {@link T}.
     * @param T {@link T} in {@code Function<T, R>}
     * @param R {@link R} in {@code Function<T, R>}
     */
    static <T, R> Supplier<R> s(T t, Function<T, R> f) {return () -> f.apply(t);}
}
