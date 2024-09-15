package com.matzua.util;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class InvertibleManifold<K, V> {
    private final Map<K, Set<V>> valuesByKey;
    private final Map<V, Set<K>> keysByValue;
    private final Set<Integer> relationships;

    public InvertibleManifold() {
        this(new HashMap<>(), new HashMap<>());
    }

    InvertibleManifold(Map<K, Set<V>> valuesByKey, Map<V, Set<K>> keysByValue) {
        relationships = new HashSet<>();

        this.valuesByKey = valuesByKey.entrySet()
            .stream()
            .filter(entry -> !entry.getValue().isEmpty())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, this::addAll, HashMap::new));

        this.keysByValue = keysByValue.entrySet()
            .stream()
            .filter(entry -> !entry.getValue().isEmpty())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, this::addAll, HashMap::new));

        this.valuesByKey.forEach((key, values) -> values.stream()
            .map(value -> hash(key, value))
            .forEach(relationships::add));

        this.keysByValue.forEach((key, values) -> values.stream()
            .map(value -> hash(value, key))
            .forEach(relationships::add));
    }

    private int hash(K k, V v) { return Objects.hash(k, v); }

    private <R> Set<R> addAll(Set<R> oldSet, Set<R> newSet) {
        oldSet.addAll(newSet);// ? newSet.size() : 0;
        return oldSet;
    }

    private <T> Set<T> removeAll(Set<T> existingSet, Set<T> targetSet) {
        existingSet.removeAll(targetSet);// ? targetSet.size() : 0;
        return existingSet.isEmpty() ? null : existingSet;
    }

    public int size() { return relationships.size(); }

    public void map(@NonNull K k, @NonNull V v) {
        relationships.add(hash(k, v));
        valuesByKey.merge(k, new HashSet<>(Set.of(v)), this::addAll);
        keysByValue.merge(v, new HashSet<>(Set.of(k)), this::addAll);
    }

    public void unmap(@NonNull K k, @NonNull V v) {
        relationships.remove(hash(k, v));

        if (valuesByKey.containsKey(k) && keysByValue.containsKey(v)) {
            valuesByKey.merge(k, new HashSet<>(Set.of(v)), this::removeAll);
            keysByValue.merge(v, new HashSet<>(Set.of(k)), this::removeAll);
        }
    }

    public Set<V> values(K k) {
        return Optional.of(k)
            .map(valuesByKey::get)
            .map(Collections::unmodifiableSet)
            .orElse(Collections.emptySet());
    }

    public Set<K> keys(V v) {
        return Optional.of(v)
            .map(keysByValue::get)
            .map(Collections::unmodifiableSet)
            .orElse(Collections.emptySet());
    }
}
