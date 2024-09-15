package com.matzua.engine.behavior;

import com.matzua.engine.core.Layer;
import com.matzua.engine.event.Event;
import processing.core.PGraphics;

public interface Component {
    default boolean onEvent(Event event) { return false; }
    default void onRender(PGraphics g) {}
    default void onUpdate() {}
    default void onTick() {}
    default boolean isContainedBy(Layer layer) { return false; }
}
