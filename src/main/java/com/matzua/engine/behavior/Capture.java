package com.matzua.engine.behavior;

import com.matzua.engine.core.EntityManager;
import com.matzua.engine.core.Layer;
import com.matzua.engine.event.Event;
import lombok.AllArgsConstructor;
import processing.core.PGraphics;

import java.util.Optional;

import static com.matzua.util.FunctionalUtils.biFunction;


@AllArgsConstructor
public class Capture implements Component {
    private final int owner;
    private final EntityManager entityManager;
    private final PGraphics target;
    private final Layer layer;
    public int getPos(EntityManager entityManager) {
        entityManager.get(Position.class).from(owner);
        return 0;
    }

    @Override
    public boolean onEvent(Event event) {
        return false;
    }

    @Override
    public void onRender(PGraphics graphics) {
        if (graphics == target && areDependenciesMet()) {
            System.out.println("RENDER CAPTURE");
            Position p = entityManager.get(Position.class).from(owner).orElseThrow();
            //graphics.translate(-p.getX(), -p.getY(), -p.getZ());

        }
    }

    @Override
    public void onUpdate() {

    }

    private boolean areDependenciesMet() {
        return Optional.of(Position.class)
            .map(entityManager::get)
            .flatMap(biFunction(EntityManager.ComponentGetter<Position>::from).bind(owner))
            .isPresent();
    }
}
