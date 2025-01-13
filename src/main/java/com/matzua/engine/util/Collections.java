package com.matzua.engine.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Collections {
    static <T, L extends List<T>> L addAll(L sink, int index, L source) {
        sink.addAll(index, source);
        return sink;
    }
    static <T, C extends Collection<T>> C addAll(C sink, C source) {
        sink.addAll(source);
        return sink;
    }
}
