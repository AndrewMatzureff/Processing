package com.matzua.jpg.user.simple;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.IGameState;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.draw.IRenderer;
import com.matzua.jpg.core.app.resource.common.AbstractResourceStore;
import com.matzua.jpg.core.sys.ISystemRenderer;
import com.matzua.jpg.user.state.Entity;

public interface SimpleEventLoopSystemRenderer extends ISystemRenderer {
//    IRenderer getRenderer();
    IGameState getGameState();
    IEventManager getEventManager();
    AbstractResourceStore<ICanvas> getCanvasStore();
    @Override
    default void draw() {
        final AbstractResourceStore<ICanvas> canvasStore = getCanvasStore();
        final IEventManager eventManager = getEventManager();
        final IGameState gameState = getGameState();
//        final IRenderer renderer = getRenderer();
        gameState.render();
        gameState.update();
        eventManager.flush();
    }
}
