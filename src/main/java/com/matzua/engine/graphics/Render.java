package com.matzua.engine.graphics;

import com.matzua.engine.behavior.Component;
import com.matzua.engine.behavior.Position;
import com.matzua.engine.core.EntityManager;
import processing.core.PGraphics;

public class Render implements Component {
    EntityManager entityManager = new EntityManager();
    @Override
    public void onRender(PGraphics g) {
        entityManager.get(Position.class).from(0);
    }
}
