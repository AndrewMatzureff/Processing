package com.matzua;

import processing.core.PGraphics;

import java.util.List;
import java.util.function.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Cloud {
    public record Point(float x, float y, float z, int color) {}

    private final List<Point> points;

    private float x = 0;

    private float y = 0;

    private float z = 0;

    private boolean lines = false;

    public static Cloud of(Stream<Point> points, boolean lines) {
        return new Cloud(points.toList(), lines);
    }

    public static Stream<Point> circle(float radius, float x, float y, float z, int density) {
        return IntStream.range(0, 360 / density)
                .map(i -> i * density)
                .mapToDouble(Math::toRadians)
                .mapToObj(d -> new Point(
                        x + (float) Math.cos(d) * radius,
                        y + (float) Math.sin(d) * radius,
                        z,
                        (int) (Math.toDegrees(d) / 180 * 0x01000000) | 0xff000000
                ));
    }

    public static Stream<Point> sphere(float radius, float x, float y, float z, int density) {
        return IntStream.range(0, 180 / density)
            .map(i -> i * density)
            .mapToDouble(Math::toRadians)
            .mapToObj(d -> circle((float) Math.sin(d) * radius, x, y, z + (float) Math.cos(d) * radius - 0, (int) (density / ((float) Math.sin(d) * radius / radius))))
            .flatMap(Function.identity());
    }

    public static Stream<Point> fill(
            float radius,
            float x,
            float y,
            float z,
            int density,
            UnaryOperator<Float> radiusFiller,
            UnaryOperator<Float> angleFiller
    ) {
        return IntStream.range(0, (int) (4 / 3f * Math.PI * Math.pow(radius, 3)) / density)
                .mapToObj(i -> {
                    final float r = radiusFiller.apply(radius);
                    final float xy = angleFiller.apply((float) (Math.PI * 2));
                    final float zangle = angleFiller.apply((float) (Math.PI * 2));

                    final double cosxy = Math.cos(xy);
                    final double sinxy = Math.sin(xy);

                    return new Point(
                            x + (float) (cosxy * r) * r * r / radius,
                            y + (float) (sinxy * r) * r * r / radius,
                            z - radius + (float) (Math.tan(zangle)) * r + r * r * r * (float) Math.sin(r) / radius,
                            0xff000000 | (i * density)
                    );
                });
    }

    public Cloud(List<Point> points) {
        this.points = points;
    }
    public Cloud(List<Point> points, boolean lines) {
        this(points);
        this.lines = lines;
    }

    public void draw(PGraphics g) {
        g.push();
        int offset = (int) (System.currentTimeMillis());
        if (lines) {
            points.stream()
                .reduce((a, b) -> {
                    final float xpa = x + a.x;
                    final float ypa = y + a.y;
                    final float xpb = x + b.x;
                    final float ypb = y + b.y;
                    g.stroke((((a.color & 0x00ffffff) + (b.color & 0x00ffffff)) / 2 + offset) | 0xff000000);//(point.color + g.millis()) | 0xff000000
                    g.line(xpa, ypa, z + a.z, xpb, ypb, z + b.z);
                    return b;
                }).ifPresent(p -> {});
        } else {
            points.forEach(point -> {
                final float xp = x + point.x;
                final float yp = y + point.y;
                g.fill(point.color);
                g.stroke((((point.color & 0x00ffffff) + (point.color & 0x00ffffff)) / 2 + offset) | 0xff000000);//(point.color + g.millis()) | 0xff000000
                g.line(xp, yp, z + point.z, xp, yp, z + point.z + 1);
            });
        }

        g.pop();
    }
    // a = 2arctan(1/e_z)
    // tan
    // b_x = e_z * d_x / d_z + e_x

    // b_x = d_x * s_x / (d_z * r_x) * r_z
    // b_y = d_y * s_y / (d_z * r_y) * r_z
}
