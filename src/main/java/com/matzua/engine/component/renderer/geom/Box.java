package com.matzua.engine.component.renderer.geom;

import com.matzua.engine.component.scene.Position;
import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.Component;
import com.matzua.engine.entity.EntityManager;
import com.matzua.engine.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Box implements Component {
    private final EventManager eventManager;
    private final EntityManager entityManager;
    private final float size;
    @Override
    public void onTick(Id<?> id) {
        entityManager.message(
            Component.id(
                id.entity(),
                "main",
                Position.class
            ),
            position -> eventManager.dispatch(
                new Event.Render.Box(
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    size
                )
            )
        );
    }
}
