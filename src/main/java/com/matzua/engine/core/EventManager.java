package com.matzua.engine.core;

import com.matzua.engine.event.Event;
import com.matzua.engine.util.Collections;
import com.matzua.engine.util.Validation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Builder(setterPrefix = "with")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor(onConstructor_ = {@Inject})
public class EventManager {
    Map<Class<? extends Event>, List<Consumer<? extends Event>>> subscribers;
    Map<Class<?>, Function<?, ? extends Event>> adapters;
    static final Function<Object, Event> toEvent = e -> Optional.of(e).map(Event.class::cast).orElseThrow();
    public <T extends Event> void subscribe(Class<T> type, Consumer<T> subscriber) {
        // TODO: add subscribers to the buckets of all parents of type
        subscribers.merge(type, new LinkedList<>(List.of(subscriber)), Collections::addAll);
    }
    public <T, U extends Event> void adapt(Class<T> type, Function<T, U> adapter) {
        if (Event.class.isAssignableFrom(type)) {
            // No need to adapt a domain Event.
            throw Validation.newPlaceholderError();
        }
        // TODO: add adapters to the buckets of all parents of type
        adapters.put(type, adapter);
    }

    public <T> void dispatch(@NonNull final T event) {
        final Event domainEvent = (Event) (Optional.of(event)
            .map(Object::getClass)
            .map(adapters::get)
            .map(Function.class::cast)
            .map(Function.<Function<Object, Event>>identity()::apply)
            .orElse(toEvent)
            .apply(event));

        Optional.of(domainEvent)
            .map(Event::getClass)
            .map(subscribers::get)
            .orElse(java.util.Collections.emptyList())
            .stream()
            .map(Consumer.class::cast)
            .forEach(domainEvent::consume);
    }
}
