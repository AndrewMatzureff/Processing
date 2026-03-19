package com.matzua.engine.app.simple;

import com.matzua.engine.app.interfaces.IEventManager;

import java.util.*;
import java.util.function.Consumer;

public class SimpleEventManager implements IEventManager {
    private final Map<Class<?>, List<Consumer<?>>> subscribers;
    public SimpleEventManager(Map<Class<?>, List<Consumer<?>>> subscribers) {
        this.subscribers = subscribers;
    }
    @Override
    public <T> void dispatch(T event) {
        Optional.of(event)
            .map(Object::getClass)
            .map(subscribers::get)
            .orElse(java.util.Collections.emptyList())
            .stream()
            .map(Consumer.class::<Consumer<T>>cast)
            .forEach(consumer -> consumer.accept(event));
    }
    @Override
    public <T> void subscribe(Class<T> target, Consumer<T> subscriber) {
        subscribers.merge(target, new ArrayList<>(List.of(subscriber)), (existing, latest) -> {
            existing.addAll(latest);
            return existing;
        });
    }
}
