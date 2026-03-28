package com.matzua.jpg.user.state;

import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.IRenderer;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import lombok.ToString;
import processing.core.PGraphics;

@ToString
public class Entity {
    private final PGraphicsCanvasStore canvasStore;
    private final IEventManager eventManager;
    public Entity(PGraphicsCanvasStore canvasStore, IEventManager eventManager) {
        this.canvasStore = canvasStore;
        this.eventManager = eventManager;
        eventManager.subscribe(Type.extending(IRenderer.class), IRenderer::render);
    }
    public void update() {
        // Apply Behaviors & Interactions
        /*...*/
        // Ready for Rendering
        final IRenderer renderer = new IRenderer() {
            @Override
            public void render() {
                System.out.println(this);
                final PGraphics g = canvasStore.get("main").pGraphics();
                g.  push();
                g.      stroke(255);
                g.      line(0,0,g.width,g.height);
                g.      line(g.width,0,0,g.height);
                g.  pop();
            }
        };
        eventManager.dispatch(renderer);
    }
}
