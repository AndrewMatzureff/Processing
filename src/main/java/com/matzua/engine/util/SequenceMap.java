package com.matzua.engine.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class SequenceMap<K, V> implements Map<K, List<V>> {
    @Delegate
    private final Map<K, List<V>> map;
    private final UnaryOperator<List<V>> copy;
    public SequenceMap(Supplier<Map<K, List<V>>> mapFactory, UnaryOperator<List<V>> listCopier) {
        map = mapFactory.get();
        copy = listCopier;
    }
    public void putAt(K k, V v, int i) {
        this.merge(k, copy.apply(List.of(v)), (a, b) -> Collections.addAll(a, i, b));
    }
    public void putFirst(K k, V v) {
        this.putAt(k, v, 0);
    }
    public void putLast(K k, V v) {
        this.putAt(k, v, Optional.of(k)
            .map(this::get)
            .map(List::size)
            .map(Math::decrementExact)
            .orElse(0));
    }
    public Stream<V> stream(K k) {
        return this.getOrDefault(k, List.of()).stream();
    }
    public static class Impl<K, V> extends SequenceMap<K, V>{
        public Impl(Supplier<Map<K, List<V>>> mapFactory, UnaryOperator<List<V>> listCopier) {
            super(mapFactory, listCopier);
        }
        @Inject
        public Impl(Map<K, List<V>> map, UnaryOperator<List<V>> copy) {
            super(map, copy);
        }
    }
}
