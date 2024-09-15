package com.matzua.engine;

import com.matzua.engine.core.Layer;
import com.matzua.engine.event.Event;
import com.matzua.util.FunctionalUtils;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.opengl.PGraphicsOpenGL;

import javax.inject.Inject;
import java.util.*;

public class App extends PApplet {
    PGraphics pg;
    private final Deque<Layer> layers;

    @Inject
    public App(Deque<Layer> layers) {
        this.layers = layers;
    }

    public void settings() {
        size (640, 360, P2D);
    }

    public void setup() {
        windowTitle("movement: 'W', 'A', 'S', 'D', 'C', 'SPACE', 'SHIFT'; field of view: '-', '='");
        layers.forEach(layer -> layer.onAttach(this));

        if (g.isGL()) {
            ((PGraphicsOpenGL) g).textureSampling(2);
        }
    }

    public PGraphics getOrCreateCaptureTarget() {
        if (pg != null) {
            return pg;
        }
        // TODO: track capture targets in use so that graphics resources can be freed.
        pg = createGraphics(320, 180, P3D);

        if (pg.isGL()) {
            ((PGraphicsOpenGL) pg).textureSampling(2);
        }

        return pg;
    }

    public void draw() {
        push();
        tick();
        render();
        image(pg, 0, 0, width, height);
        pop();
    }

    public void pushLayer(Layer layer) {
        System.out.println("PUSH LAYER");
        layers.push(layer);
    }

    public void tick() {
        layers.forEach(Layer::onTick);
    }

    public void render() {
        System.out.println("RENDER APP");
        pg.beginDraw();
        pg.background(255);
        pg.push();
        layers.descendingIterator()
            .forEachRemaining(layer -> layer.onRender(pg));
        pg.pop();
        pg.endDraw();
    }

    public boolean dispatch(Event event) {
        return layers.stream()
            .map(FunctionalUtils.biFunction(Layer::onEvent).bind(event))
            .filter(Boolean.TRUE::equals)
            .findFirst()
            .orElse(false);
    }

    public void update() {
        layers.forEach(Layer::onUpdate);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        if (keyEvent.getKey() == CODED) {
            return;
        }

        dispatch(Event.builder()
            .withTags(Event.KEY_TYPED)
            .withState(keyEvent.getKey())
            .build());
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        dispatch(Event.builder()
            .withTags(Event.KEY_PRESSED)
            .withState(keyEvent.getKeyCode())
            .build());
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        dispatch(Event.builder()
            .withTags(Event.KEY_RELEASED)
            .withState(keyEvent.getKeyCode())
            .build());
    }
}