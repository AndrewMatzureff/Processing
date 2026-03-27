package com.matzua.jpg.user.state;

import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.IRenderer;
import lombok.ToString;

@ToString
public class Entity {
    private final IEventManager eventManager;
    public Entity(IEventManager eventManager) {
        this.eventManager = eventManager;
        eventManager.subscribe(Type.of(IRenderer.class), IRenderer::render);
    }
    public void update() {
        // Apply Behaviors & Interactions
        /*...*/
        // Ready for Rendering
        final IRenderer renderer = new IRenderer() {
            @Override
            public void render() {
                System.out.println(this);
            }
        };
        eventManager.dispatch(renderer);
    }
}
