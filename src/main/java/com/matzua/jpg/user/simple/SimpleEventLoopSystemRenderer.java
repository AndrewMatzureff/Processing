package com.matzua.jpg.user.simple;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.IGameLoop;
import com.matzua.jpg.core.app.IRenderer;
import com.matzua.jpg.core.sys.ISystemRenderer;
import com.matzua.jpg.user.state.Entity;

public interface SimpleEventLoopSystemRenderer extends ISystemRenderer {
    IRenderer getRenderer();
    IGameLoop getGameLoop();
    IEventManager getEventManager();
    PGraphicsCanvasStore getCanvasStore();
    @Override
    default void draw() {
        final PGraphicsCanvasStore canvasStore = getCanvasStore();
        final IEventManager eventManager = getEventManager();
        final IGameLoop gameLoop = getGameLoop();
        final IRenderer renderer = getRenderer();
//        gameLoop.update();
        eventManager.flush();
//        renderer.render();
        // TODO: ↓ delete this... ↓
        final ICanvas root = canvasStore.get("root");
        final ICanvas main = canvasStore.get("main");
        main.pGraphics().beginDraw();
        main.pGraphics().fill((int) System.currentTimeMillis() / 10 | 0xff000000);
        main.pGraphics().rect(0,0,main.pGraphics().width,main.pGraphics().height);
        new Entity(canvasStore, eventManager).update();
        main.pGraphics().endDraw();
        root.pGraphics().image(main.pGraphics(), 0, 0, root.pGraphics().width, root.pGraphics().height);
    }
}
