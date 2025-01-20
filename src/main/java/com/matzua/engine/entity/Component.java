package com.matzua.engine.entity;

import com.matzua.engine.util.Types;

import java.util.function.Consumer;

public interface Component {
    record Id<T extends Component>(String descriptor, Class<T> type) {}
    static <T extends Component> Id<T> id(String descriptor, Class<T> type) {
        return new Id<>(descriptor, type);
    }
    void onTick();
    default <T extends Component> void onMessage(Consumer<T> message) {
        Types.cast(message).accept(this);
    }
}
