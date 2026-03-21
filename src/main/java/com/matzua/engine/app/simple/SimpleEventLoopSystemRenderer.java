package com.matzua.engine.app.simple;

import com.matzua.engine.app.interfaces.IEventManager;
import com.matzua.engine.app.interfaces.IGameLoop;
import com.matzua.engine.app.interfaces.IRenderer;
import com.matzua.engine.app.interfaces.ISystemRenderer;

public interface SimpleEventLoopSystemRenderer extends ISystemRenderer {
    IRenderer getRenderer();
    IGameLoop getGameLoop();
    IEventManager getEventManager();
    default void draw() {


    }
}
