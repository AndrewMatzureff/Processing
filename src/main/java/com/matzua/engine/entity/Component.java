package com.matzua.engine.entity;

import java.util.UUID;
import java.util.function.Consumer;

import static com.matzua.engine.util.Types.cast;

public interface Component {
    // TODO: possibly consider allowing multiple descriptors somehow (i.e.: tags)...
    record Id<T extends Component>(UUID entity, String descriptor, Class<T> type) {
        public <U extends Component> Id<U> companion(Class<U> type) {
            return new Id<>(this.entity, this.descriptor, type);
        }
        public Id<T> companion(String descriptor) {
            return new Id<>(this.entity, descriptor, this.type);
        }
        public Id<T> companion(UUID entity) {
            return new Id<>(entity, this.descriptor, this.type);
        }
        public Id<T> companion(UUID entity, String descriptor) {
            return new Id<>(this.entity, descriptor, this.type);
        }
        public <U extends Component> Id<U> companion(String descriptor, Class<U> type) {
            return new Id<>(this.entity, descriptor, type);
        }
        public <U extends Component> Id<U> companion(UUID entity, Class<U> type) {
            return new Id<>(entity, this.descriptor, type);
        }
    }
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
