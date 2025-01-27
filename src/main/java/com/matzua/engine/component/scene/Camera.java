package com.matzua.engine.component.scene;

import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.Component;
import com.matzua.engine.entity.EntityManager;
import com.matzua.engine.event.Event;

public class Camera implements Component {
    private final EventManager eventManager;
    private final EntityManager entityManager;

    public Camera(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
    }

    @Override
    public void onTick(Id<?> id) {
        entityManager.message(
            Component.id(
                id.entity(),
                "main",
                Position.class
            ),
            position -> {
                eventManager.dispatch(
                    new Event.Camera(
                        position.getX(),
                        position.getY(),
                        0
                    )
                );
            }
        );
    }
}
