package com.matzua.jpg.user.state;

import java.util.function.BiPredicate;
import java.util.function.Function;

/** This class represents a description of a class, or range of classes along a branch of the class hierarchy, which
 * can be used to match a given class based on its relationship to the class or range described. Since this class
 * extends {@link Record}, instances may serve as stable {@link java.util.Map} keys.
 * @param clazz describes a class or range of classes against which to match other instances of {@link Class}
 * @param relation describes the relationship required to consider another instance of {@link Class} to be a match
 * @param <T> describes the generic type of the {@link Class} used as a point of reference for the modeled relationship
 */
public record Type<T>(Class<T> clazz, Relation relation) {
    /** This enum represents all possible class relationships supported by {@link Type} for matching.
     */
    public enum Relation {
        /** You can model a type "of" something directly by using {@code IS}. In this case a {@link Type} will
         * only match class instances that are equal to that of the {@link Type} in question.
         */
        IS(Type::of, Class::equals, false),

        /** You can model any type "extending" something broadly by using {@code EXTENDS}. In this case a {@link Type}
         * will only match class instances describing a subclass (but not necessarily a direct child) of the class
         * referenced in the relevant {@link Type}.
         */
        EXTENDS(Type::extending, Class::isAssignableFrom, true),

        /** You can model any type "enclosing" something broadly by using {@code ENCLOSES}. In this case a {@link Type}
         * will only match class instances describing a superclass (but not necessarily a direct parent) of the class
         * referenced in the relevant {@link Type}.
         */
        ENCLOSES(Type::enclosing, Class::isAssignableFrom, false);
        private final Function<Class<?>, Type<?>> initializer;
        private final BiPredicate<Class<?>, Class<?>> matcher;
        Relation(
            Function<Class<?>, Type<?>> initializer, BiPredicate<Class<?>, Class<?>> matcher, boolean swap
        ) {
            this.initializer = initializer;
            this.matcher = swap ? (a, b) -> matcher.test(b, a) : matcher;   /* Swapped matcher is required for 'extends'
                                                                             * since Class::isAssignableFrom is reused.
                                                                             */
        }
    }

    /** This method is used to determine whether the given {@link Class} is a match to this {@link Type}.
     * @param clazz the given {@link Class} to test
     * @return whether the given {@link Class} falls under the purview of this {@link Type}
     */
    public boolean matches(Class<?> clazz) {
        return relation().matcher.test(clazz, clazz());
    }

    /** Use this utility to instantiate a new {@link Type} which matches based on the following criteria:
     * "{@code any class instance equal to the given class}".
     * @param clazz the given class
     * @return a new {@link Type} modeling the "{@link Relation#IS}" relationship to the given class
     * @param <T> the generic type of the given class
     */
    public static <T> Type<T> of(Class<T> clazz) {return type(clazz, Relation.IS);}

    /** Use this utility to instantiate a new {@link Type} which matches based on the following criteria:
     * "{@code any class instance which is a supertype of the given class}".
     * @param clazz the given class
     * @return a new {@link Type} modeling the "{@link Relation#EXTENDS}" relationship to the given class
     * @param <T> the generic type of the given class
     */
    public static <T> Type<T> extending(Class<T> clazz) {return type(clazz, Relation.EXTENDS);}

    /** Use this utility to instantiate a new {@link Type} which matches based on the following criteria:
     * "{@code any class instance which is a subtype of the given class}".
     * @param clazz the given class
     * @return a new {@link Type} modeling the "{@link Relation#ENCLOSES}" relationship to the given class
     * @param <T> the generic type of the given class
     */
    public static <T> Type<T> enclosing(Class<T> clazz) {return type(clazz, Relation.ENCLOSES);}
    private static <T> Type<T> type(Class<T> clazz, Relation relation) {
        return new Type<>(clazz, relation);
    }
    static Function<Class<?>, Type<?>> initializerByRelation(Relation relation) {
        return relation.initializer;
    }
}
