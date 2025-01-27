package com.matzua.engine.renderer;

import com.matzua.engine.core.EventManager;
import com.matzua.engine.event.Event;
import com.matzua.engine.util.Initializer;
import com.matzua.engine.util.SequenceMap;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import processing.core.PGraphics;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;

import static com.matzua.engine.util.Types.cast;

@Builder (setterPrefix = "with", buildMethodName = "defaultBuild")
@FieldDefaults (makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor (onConstructor_ = {@Inject})
public class Renderer {
    EventManager eventManager;
    @NonFinal
    PGraphics canvas;
    @NonFinal
    Event.Camera camera;
    @Builder.Default
    SequenceMap<Event.Render, Consumer<Event.Render>> operations = new SequenceMap.Impl<>(HashMap::new, LinkedList::new);
    public synchronized void render(PGraphics canvas) {
        this.canvas = canvas;
        canvas.beginDraw();
        canvas.push();
        canvas.background(255);
        synchronized (operations) {
            Optional.ofNullable(camera)
                .ifPresent(c -> canvas.translate(canvas.width / 2f - c.x(), canvas.height / 2f - c.y(), -c.z()));
            operations.forEach((k, v) -> v.forEach(cast(k::consume)));
            operations.values().forEach(List::clear);
            operations.clear();
        }
        canvas.pop();
        canvas.endDraw();
    }
    private void renderBox(Event.Render.Box e) {
        if (canvas == null) {
            return;
        }
        canvas.push();
        canvas.fill(0f);
        canvas.translate(e.x(), e.y());
        canvas.box(e.s());
        canvas.pop();
    }
    private Renderer init() {
        eventManager.subscribe(Event.Render.Camera.class, e -> camera = e);
        eventManager.subscribe(Event.Render.Box.class, e -> operations.putLast(e, cast(this::renderBox)));
        return this;
    }
    public static class RendererBuilder implements Initializer<Renderer> {
        public Renderer build() {
            return init(Renderer::init);
        }
    }
}
