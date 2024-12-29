package com.matzua.engine.event;

import java.util.function.Consumer;

public interface Event {
    interface Input extends Event {
        record Device(int axis, double state) implements Input {}
        record Text(char character) implements Input {}
    }
    default void consume(Consumer<Event> consumer) {
        consumer.accept(this);
    }
}
