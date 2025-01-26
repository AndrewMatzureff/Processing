package com.matzua.engine.component.input;

import com.matzua.engine.component.scene.Position;
import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.Component;
import com.matzua.engine.entity.EntityManager;
import com.matzua.engine.event.Event;
import processing.event.KeyEvent;

import java.util.HashMap;
import java.util.Map;

public class InputController implements Component {
    private final Map<Integer, Double> states = new HashMap<>();
    private final EntityManager entityManager;
    private final EventManager eventManager;
    public InputController(EntityManager entityManager, EventManager eventManager) {
        this.entityManager = entityManager;
        this.eventManager = eventManager;

        // Adapt raw Processing KeyEvent into internal domain Event type...
        eventManager.adapt(KeyEvent.class, e -> switch (e.getAction()) {
            case KeyEvent.PRESS -> new Event.Input.Device(e.getKeyCode(), 1.0);
            case KeyEvent.RELEASE -> new Event.Input.Device(e.getKeyCode(), 0.0);
            case KeyEvent.TYPE -> new Event.Input.Text(e.getKey());
            default -> throw new IllegalStateException("Unexpected value: " + e.getAction());
        });

        // Create subscription to update input states based on device events.
        eventManager.subscribe(Event.Input.Device.class, e -> states.put(e.axis(), e.state()));
    }
    @Override
    public void onTick(Id<?> id) {
        // Process input-dependent state changes...
        Map.of( 'W', new double[] { 0.0,-1.0},
                'A', new double[] {-1.0, 0.0},
                'S', new double[] { 0.0, 1.0},
                'D', new double[] { 1.0, 0.0})
                .entrySet()
                .stream()
                .map(e -> {
                    e.getValue()[0] *= states.getOrDefault((int) e.getKey(), 0.0);
                    e.getValue()[1] *= states.getOrDefault((int) e.getKey(), 0.0);
                    return e.getValue();
                })
                .reduce((e1, e2) -> new double[]{e1[0] + e2[0], e1[1] + e2[1]})
                .ifPresent(d -> {
                    entityManager.message(Component.id(id.entity(), "main", Position.class), position -> {
                        position.setX(position.getX() + (float) d[0]);
                        position.setY(position.getY() + (float) d[1]);
                    });
                });
        // ...
    }
}
