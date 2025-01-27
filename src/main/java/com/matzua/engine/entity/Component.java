package com.matzua.engine.entity;

import java.util.UUID;
import java.util.function.Consumer;

import static com.matzua.engine.util.Types.cast;

public interface Component {
    // TODO: possibly consider allowing multiple descriptors somehow (i.e.: tags)...
    record Id<T extends Component>(UUID entity, String descriptor, Class<T> type) {}
    static <T extends Component> Id<T> id(UUID entity, String descriptor, Class<T> type) {
        return new Id<>(entity, descriptor, type);
    }
    static <T extends Component> void tick(Id<T> id, Component component) {
        component.onTick(id);
    }
    void onTick(Id<?> id);
    default <T extends Component> void onMessage(Consumer<T> message) {
        cast(message).accept(this);
    }
    default <T extends Component> Id<T> id(UUID entity, String descriptor) {
        return new Id<>(entity, descriptor, cast(this.getClass()));
    }
}
