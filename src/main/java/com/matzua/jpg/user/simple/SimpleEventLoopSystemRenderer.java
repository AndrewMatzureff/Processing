package com.matzua.jpg.user.simple;

import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsRecipe;
import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.IGameLoop;
import com.matzua.jpg.core.app.IRenderer;
import com.matzua.jpg.core.sys.ISystemRenderer;
import processing.core.PGraphics;

import java.util.function.BiFunction;

public interface SimpleEventLoopSystemRenderer extends ISystemRenderer {
    IRenderer getRenderer();
    IGameLoop getGameLoop();
    IEventManager getEventManager();
    PGraphicsCanvasStore getCanvasStore();
    @Override
    default void draw() {
        final IEventManager eventManager = getEventManager();
        final IGameLoop gameLoop = getGameLoop();
        final IRenderer renderer = getRenderer();
//        gameLoop.update();
        eventManager.flush();
//        renderer.render();

//        fill((int) System.nanoTime() | 0xff000000);
//        rect(0,0,width,height);
    }
}
