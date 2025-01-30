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
import static com.matzua.engine.util.Validation.ifAllPresent;

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
    public void render(PGraphics canvas) {
        this.canvas = canvas;
        ifAllPresent(canvas, camera).accept(() -> {
            canvas.beginDraw();
            canvas.push();
            canvas.background(0);
            synchronized (operations) {
                float cameraZ = (canvas.height / 2f) / (float) Math.tan(camera.fov() / 2f);
                canvas.perspective(camera.fov(), canvas.width / (float) canvas.height, cameraZ * 0.1f, cameraZ * 10f);
                canvas.translate(
                    canvas.width  / 2f - camera.x(),
                    canvas.height / 2f - camera.y(),
                    -camera.z()
                );
                operations.forEach((k, v) -> v.forEach(cast(k::consume)));
                operations.values().forEach(List::clear);
                operations.clear();
            }
            canvas.pop();
            canvas.endDraw();
        });
    }
    private void renderBox(Event.Render.Box e) {
        canvas.push();
        canvas.fill(255);
        canvas.translate(e.x(), e.y(), e.z());
        canvas.box(e.s());
        canvas.pop();
    }
    public void renderWirePath(Event.Render.WirePath e) {
        int offset = (int) System.currentTimeMillis();
        canvas.push();
        e.points()
            .stream()
            .filter(point -> e.z() + point.z() != 0)
            .reduce((a, b) -> {
                final float xa = e.x() + a.x();
                final float ya = e.y() + a.y();
                final float za = e.z() + a.z();
                final float xb = e.x() + b.x();
                final float yb = e.y() + b.y();
                final float zb = e.z() + b.z();
                canvas.stroke((((a.color() & 0x00ffffff) + (b.color() & 0x00ffffff)) / 2 + offset) | 0xff000000);
                canvas.line(xa, ya, za, xb, yb, zb);
                return b;
            })
            .orElseThrow();
        canvas.pop();
    }
    // a = 2arctan(1/e_z)
    // tan
    // b_x = e_z * d_x / d_z + e_x

    // b_x = d_x * s_x / (d_z * r_x) * r_z
    // b_y = d_y * s_y / (d_z * r_y) * r_z
    private Renderer init() {
        eventManager.subscribe(Event.Render.Camera.class, e -> this.camera = e);
        eventManager.subscribe(Event.Render.WirePath.class, e -> operations.putLast(e, cast(this::renderWirePath)));
        eventManager.subscribe(Event.Render.Box.class, e -> operations.putLast(e, cast(this::renderBox)));
        return this;
    }
    public static class RendererBuilder implements Initializer<Renderer> {
        public Renderer build() {
            return init(Renderer::init);
        }
    }
}
