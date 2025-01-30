package com.matzua.engine.component.renderer.geom;

import com.matzua.engine.component.scene.Position;
import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.Component;
import com.matzua.engine.entity.EntityManager;
import com.matzua.engine.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.stream.IntStream;

@Builder (setterPrefix = "with")
@AllArgsConstructor
public class WirePath implements Component {
    public record Point(float x, float y, float z, int color) {}
    private final EventManager eventManager;
    private final EntityManager entityManager;

    private final List<Point> points;

    public static List<Point> circle(float radius, float x, float y, float z, int iDensity) {
        return IntStream.range(0, 360 / iDensity)
            .map(i -> i * iDensity)
            .mapToDouble(Math::toRadians)
            .mapToObj(d -> new Point(
                x + (float) Math.cos(d) * radius,
                y + (float) Math.sin(d) * radius,
                z,
                (int) (Math.toDegrees(d) / 180 * 0x01000000) | 0xff000000
            ))
            .toList();
    }

    public static List<Point> sphere(float radius, float x, float y, float z, int iDensity) {
        return IntStream.range(0, 180 / iDensity)
            .map(i -> i * iDensity)
            .mapToDouble(Math::toRadians)
            .mapToObj(d -> circle(
                (float) Math.sin(d) * radius,
                x,
                y,
                z + (float) Math.cos(d) * radius,
                (int) (iDensity / ((float) Math.sin(d) * radius / radius))
            ))
            .flatMap(List::stream)
            .toList();
    }
    @Override
    public void onTick(Id<?> id) {
        entityManager.message(
            Component.id(
                id.entity(),
                "main",
                Position.class
            ),
            position -> eventManager.dispatch(
                new Event.Render.WirePath(
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    points
                )
            )
        );
    }
}
