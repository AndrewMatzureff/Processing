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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.matzua.engine.util.Types.cast;

@Builder (setterPrefix = "with", buildMethodName = "defaultBuild")
@FieldDefaults (makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor (onConstructor_ = {@Inject})
public class Renderer {
    EventManager eventManager;
    @NonFinal
    PGraphics canvas;
    @Builder.Default
    SequenceMap<Event.Render, Consumer<Event.Render>> operations = new SequenceMap.Impl<>(HashMap::new, LinkedList::new);
    public synchronized void render(PGraphics canvas) {
        this.canvas = canvas;
        canvas.beginDraw();
        canvas.push();
        synchronized (operations) {
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
        eventManager.subscribe(Event.Render.Box.class, e -> operations.putLast(e, cast(this::renderBox)));
        return this;
    }
    public static class RendererBuilder implements Initializer<Renderer> {
        public Renderer build() {
            return init(Renderer::init);
        }
    }
}
