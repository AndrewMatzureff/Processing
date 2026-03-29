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
    IRenderer getRenderer();
    IGameState getGameState();
    IEventManager getEventManager();
    AbstractResourceStore getCanvasStore();
    @Override
    default void draw() {
        final AbstractResourceStore canvasStore = getCanvasStore();
        final IEventManager eventManager = getEventManager();
        final IGameState gameState = getGameState();
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
        final Entity entity = new Entity(canvasStore, eventManager);
        entity.x = 50;
        entity.y = 50;
        entity.update();
        main.pGraphics().endDraw();
        root.pGraphics().image(main.pGraphics(), 0, 0, root.pGraphics().width, root.pGraphics().height);
    }
}
