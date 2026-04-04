package com.matzua.jpg.user.state;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.draw.IRenderer;
import com.matzua.jpg.core.app.resource.common.AbstractResourceStore;
import com.matzua.jpg.user.Component;
import com.matzua.jpg.user.presentation.ISimpleRenderer;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class Entity {
    public final List<Component> components = new ArrayList<>();
    private final AbstractResourceStore<ICanvas> canvasStore;
    private final IEventManager eventManager;
    public float x = 0, y = 0;
    public IRenderer renderer;
    public Entity(AbstractResourceStore<ICanvas> canvasStore, IEventManager eventManager) {
        this.canvasStore = canvasStore;
        this.eventManager = eventManager;
        this.renderer = new ISimpleRenderer.X(this, eventManager, canvasStore);
        components.add(renderer);
//        eventManager.subscribe(Type.extending(IRenderer.class), IRenderer::render);
//        eventManager.subscribe(Type.extending(IDraw.class), draw -> {
//            if (draw.entity() == this) {
//                renderer.render();
//            }
//        });
    }
    public void update() {
        components.forEach(Component::onUpdate);
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
//        eventManager.<IDraw>dispatch(() -> this);
    }
    public void render() {
        renderer.onRender(null);
    }
}
