package com.matzua.jpg.core.app;

import com.matzua.jpg.user.state.Type;

import java.util.function.Consumer;

public interface IEventManager {
    void flush();
    <T> void dispatch(T event);
    <T> void subscribe(Type<T> target, Consumer<T> subscriber);
}
