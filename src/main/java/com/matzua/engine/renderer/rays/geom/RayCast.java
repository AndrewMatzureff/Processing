package com.matzua.engine.renderer.rays.geom;

import com.matzua.engine.component.renderer.geom.Box;
import com.matzua.engine.component.scene.Position;
import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.Component;
import com.matzua.engine.entity.EntityManager;
import com.matzua.engine.event.Event;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;

import java.util.function.Consumer;

public class RayCast {
    public interface Edge {
        record RenderEvent(float x1, float y1, float z1,
                           float x2, float y2, float z2,
                           float r,  float g,  float b) implements Event.Render {
            public void render(PGraphics canvas) {
                final PShape line = canvas.createShape();
                line.beginShape(PConstants.LINE);
                line.stroke(r, g, b);
                line.strokeWeight(5);
                line.vertex(x1, y1, z1);
                line.vertex(x2, y2, z2);
                line.endShape();
            }
        }
        record Component(
            EventManager eventManager,
            EntityManager entityManager,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float r,  float g,  float b
        ) implements com.matzua.engine.entity.Component {
            @Override
            public void onTick(Id<?> id) {
                entityManager.message(
                    id.companion(Position.class),
                    position -> eventManager.dispatch(
                        new RenderEvent(
                            x1, y1, z1,
                            x2, y2, z2,
                            r,  g,  b
                        )
                    )
                );
            }
        }
    }
}
