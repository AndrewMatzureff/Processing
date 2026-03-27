package com.matzua.jpg.user.state;

public record Type<T>(Class<T> clazz, Relation relation) {
    public enum Relation {
        IS, EXTENDS, ENCLOSES
    }
    public boolean matches(Class<?> clazz) {
        return switch (relation) {
            case IS -> clazz.equals(clazz());
            case EXTENDS -> clazz.isAssignableFrom(clazz());
            case ENCLOSES -> clazz().isAssignableFrom(clazz);
        };
    }
    public static <T> Type<T> of(Class<T> clazz) {return type(clazz, Relation.IS);}
    public static <T> Type<T> extending(Class<T> clazz) {return type(clazz, Relation.EXTENDS);}
    public static <T> Type<T> enclosing(Class<T> clazz) {return type(clazz, Relation.ENCLOSES);}
    private static <T> Type<T> type(Class<T> clazz, Relation relation) {
        return new Type<>(clazz, relation);
    }
}
