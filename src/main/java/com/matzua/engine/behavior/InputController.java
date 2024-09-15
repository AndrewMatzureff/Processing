package com.matzua.engine.behavior;

import com.matzua.engine.core.EntityManager;
import com.matzua.engine.event.Event;
import com.matzua.util.FunctionalUtils;
import lombok.RequiredArgsConstructor;
import processing.core.PGraphics;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static processing.core.PConstants.SHIFT;

@RequiredArgsConstructor
public class InputController implements Component {
    Set<Integer> keys = new HashSet<>();
    final int owner;
    final EntityManager entityManager;
    @Override
    public boolean onEvent(Event event) {
        return Optional.of(event)
            .filter(FunctionalUtils.biPredicate(Event::is).bind(Event.KEY_PRESSED))
            .map(Event::getState)
            .map(Integer.class::cast)
            .map(keys::add).orElse(false)
            || Optional.of(event)
            .filter(FunctionalUtils.biPredicate(Event::is).bind(Event.KEY_RELEASED))
            .map(Event::getState)
            .map(Integer.class::cast)
            .map(keys::remove).orElse(false);
    }

    @Override
    public void onRender(PGraphics g) {

    }

    @Override
    public void onTick() {
        float ds = keys.contains(SHIFT) ? 25 : 1;
        float df = (keys.contains((int) '-') || keys.contains((int) '_') ? -(float) Math.PI / 64 : 0)
            + (keys.contains((int) '=') || keys.contains((int) '+') ? +(float) Math.PI / 64 : 0);
        float dx = (keys.contains((int) 'a') ? -10 : 0) + (keys.contains((int) 'd') ? 10 : 0);
        float dy = (keys.contains((int) ' ') ? -10 : 0) + (keys.contains((int) 'c') ? 10 : 0);
        float dz = (keys.contains((int) 's') ? 10 : 0) + (keys.contains((int) 'w') ? -10 : 0);

//        camera.fov = camera.fov + df < 0 ? 0 : camera.fov + df > Math.PI ? (float) Math.PI : camera.fov + df;
//        camera.x += dx * ds;
//        camera.y += dy * ds;
//        camera.z += dz * ds;

        entityManager.get(Position.class).from(owner).ifPresent(position -> {
            System.out.println(position);
            // TODO:
            //  - switch from direct component-to-component manipulation to event/message-based approach
            //  - consider removing the ability of components to do anything at all beyond simply storing state
            position.setX(dx + position.getX());
            position.setY(dy + position.getY());
            position.setZ(dz + position.getZ());
        });
    }
}
