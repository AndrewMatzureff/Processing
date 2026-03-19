package com.matzua.engine.app.interfaces;

import java.util.function.Consumer;

public interface IEventManager {
    <T> void dispatch(T event);
    <T> void subscribe(Class<T> target, Consumer<T> subscriber);
}
