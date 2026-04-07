package com.matzua.jpg.user;

import com.matzua.jpg.core.app.ICanvas;

import java.util.Map;

public interface Component {
    default void onUpdate() {}
    void onRender(ICanvas canvas);
    default void onEvent() {}
    default void postRender(ICanvas canvas, Map<String, ICanvas> canvasesByChannel) {}
    default String channel() {return "default";}
}
