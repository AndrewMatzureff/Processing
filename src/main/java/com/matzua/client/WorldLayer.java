package com.matzua.client;

import com.matzua.engine.App;
import com.matzua.engine.core.EntityManager;
import com.matzua.engine.core.Layer;
import com.matzua.engine.event.Event;
import com.matzua.engine.graphics.Render;
import lombok.AllArgsConstructor;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

import static com.matzua.util.FunctionalUtils.biConsumer;

@AllArgsConstructor
public class WorldLayer implements Layer {
    private final EntityManager entityManager;

    @Override
    public void onAttach(App app) {
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onRender(PGraphics g) {
        System.out.println("RENDER LAYER");
        List<Render> renders = new ArrayList<>();
        entityManager.get(Render.class).forEach(renders::add, this);
        System.out.println(renders);
        renders.forEach(System.out::println);
        entityManager.get(Render.class).forEach(biConsumer(Render::onRender).bind(g), this);
    }

    @Override
    public boolean onEvent(Event event) {
        return entityManager.dispatch(event);
    }

    @Override
    public void onUpdate() {
    }
}
