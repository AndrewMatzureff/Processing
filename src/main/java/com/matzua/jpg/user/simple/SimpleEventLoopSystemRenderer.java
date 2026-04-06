package com.matzua.jpg.user.simple;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.IGameState;
import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.resource.AbstractResourceStore;
import com.matzua.jpg.core.sys.ISystemRenderer;

public interface SimpleEventLoopSystemRenderer extends ISystemRenderer {
    IGameState getGameState();
    IEventManager getEventManager();
    AbstractResourceStore<ICanvas> getCanvasStore();
    @Override
    default void draw() {
        final AbstractResourceStore<ICanvas> canvasStore = getCanvasStore();
        final IEventManager eventManager = getEventManager();
        final IGameState gameState = getGameState();
        gameState.render();
        gameState.update();
        eventManager.flush();
    }
}
