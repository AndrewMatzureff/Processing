package com.matzua.jpg.user.state;

import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.draw.IDraw;
import com.matzua.jpg.core.app.draw.IRenderer;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import com.matzua.jpg.core.app.resource.common.AbstractResourceStore;
import lombok.ToString;
import processing.core.PGraphics;

@ToString
public class Entity {
    private final AbstractResourceStore canvasStore;
    private final IEventManager eventManager;
    public float x = 0, y = 0;
    private final IRenderer renderer = new IRenderer() {
    @Override
    public void render() {
//        System.out.println(this);
        final PGraphics g = canvasStore.get("main").pGraphics();
        g.push();
        g.  stroke(255);
        g.  line(x - 5, y - 5, x + 5, y + 5);
        g.  line(x + 5, y - 5, x - 5, y + 5);
        g.pop();
    }
};
    public Entity(AbstractResourceStore canvasStore, IEventManager eventManager) {
        this.canvasStore = canvasStore;
        this.eventManager = eventManager;
//        eventManager.subscribe(Type.extending(IRenderer.class), IRenderer::render);
        eventManager.subscribe(Type.extending(IDraw.class), draw -> {
            if (draw.entity() == this) {
                renderer.render();
            }
        });
    }
    public void update() {
        // Apply Behaviors & Interactions
        /*...*/
        // Ready for Rendering
//        final IRenderer renderer = new IRenderer() {
//            @Override
//            public void render() {
//                System.out.println(this);
//                final PGraphics g = canvasStore.get("main").pGraphics();
//                g.push();
//                g.  stroke(255);
//                g.  line(x - 5, y - 5, x + 5, y + 5);
//                g.  line(x + 5, y - 5, x - 5, y + 5);
//                g.pop();
//            }
//        };
        eventManager.<IDraw>dispatch(() -> this);
    }
}
