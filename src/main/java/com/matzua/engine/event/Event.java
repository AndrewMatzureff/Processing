package com.matzua.engine.event;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.matzua.engine.util.Types.cast;

public interface Event {
    interface Input extends Event {
        record Device(int axis, double state) implements Input {}
        record Text(char character) implements Input {}
    }
    interface Render extends Event {
        default <T> Consumer<Event> withGraphics(T canvas, BiConsumer<T, Render> operation) {
            return cast((Render e) -> operation.accept(canvas, e));
        }
        record Box(float x, float y, float s) implements Render {}
    }
    default void consume(Consumer<Event> consumer) {
        consumer.accept(this);
    }
    record Camera(float x, float y, float z) implements Event {}
}
