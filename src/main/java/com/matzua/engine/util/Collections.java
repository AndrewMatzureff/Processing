package com.matzua.engine.util;

import java.util.Collection;

public interface Collections {
    static <T, C extends Collection<T>> C addAll(C sink, C source) {
        sink.addAll(source);
        return sink;
    }
}
