package com.matzua.engine.core;

import com.matzua.engine.App;
import com.matzua.engine.behavior.Component;
import com.matzua.engine.event.Event;
import processing.core.PGraphics;

import java.util.function.Predicate;

public interface Layer {
    default void onAttach(App app) {}
    default void onTick() {}
    default void onRender(PGraphics g) {}
    default boolean onEvent(Event event) { return false; }
    default void onUpdate() {}
    default boolean contains(Component component) { return component.isContainedBy(this); }
    interface Filter extends Component, Predicate<Layer> {
        //
    }
}
