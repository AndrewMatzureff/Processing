package com.matzua.jpg.core.app.resource.common;

import java.util.function.BiPredicate;
import java.util.function.Function;

public interface ResourceFactory<T> {
    T create();
}
