package com.matzua.jpg.user.simple;

import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.user.state.Type;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;

@AllArgsConstructor (onConstructor = @__({@Inject}))
public class SimpleEventManager implements IEventManager {
    private final Map<Type<?>, List<Consumer<?>>> subscribers;
    @Override
    public void flush() {}

    @Override
    public <T> void dispatch(T event) {
        Optional.of(event)
            .map(Object::getClass)
            .map(this::getRelevantSubscribers)
            .orElse(java.util.Collections.emptyList())
            .stream()
            .map(Consumer.class::<Consumer<T>>cast)
            .forEach(consumer -> consumer.accept(event));
    }
    @Override
    public <T> void subscribe(Type<T> target, Consumer<T> subscriber) {
        subscribers.merge(target, new ArrayList<>(List.of(subscriber)), (existing, latest) -> {
            existing.addAll(latest);
            return existing;
        });
    }

    private List<Consumer<?>> getRelevantSubscribers(Class<?> clazz) {
        return subscribers
            .entrySet()
            .stream()
            .filter(e -> e.getKey().matches(clazz))
            .map(Map.Entry::getValue)
            .flatMap(List::stream)
            .toList();
    }
}
