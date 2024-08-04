package com.matzua;

import processing.core.PApplet;

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
                        .mapToObj(d -> circle((float) Math.sin(d) * radius, x, y, z + (float) Math.cos(d) * radius - radius, (int) (density / ((float) Math.sin(d) * radius / radius))))
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
//                            z - radius + (float) (Math.tan(zangle)) * r * r * r / radius,
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

    public void draw(PApplet g, Camera camera) {g.
        g.push();
        int offset = (int) ((camera.x * camera.y - Math.pow(camera.z, 2)) + g.millis());
        if (lines) {
            points.stream()
                    .filter(point -> z + point.z - camera.z > 0)
                    .reduce((a, b) -> {
                        final float xpa = camera.projectX(x + a.x, z + a.z, g.width);
                        final float ypa = camera.projectY(y + a.y, z + a.z, g.height);
                        final float xpb = camera.projectX(x + b.x, z + b.z, g.width);
                        final float ypb = camera.projectY(y + b.y, z + b.z, g.height);
                        g.stroke((((a.color & 0x00ffffff) + (b.color & 0x00ffffff)) / 2 + offset) | 0xff000000);//(point.color + g.millis()) | 0xff000000
//                    g.rect(xp, yp, rectDim, rectDim);
                        g.line(xpa, ypa, xpb, ypb);
                        //g.line(xp + rectDim / 2, yp + rectDim / 2, g.width / 2f, g.height / 2f);
                        return b;
                    }).ifPresent(p -> {});
//                });
        } else {
            points.stream()
                    .filter(point -> z + point.z - camera.z > 0)
                    .forEach(point -> {
                        final float rectDim = camera.project(100, z + point.z);
                        final float xp = camera.projectX(x + point.x, z + point.z, g.width) - rectDim / 2;
                        final float yp = camera.projectY(y + point.y, z + point.z, g.height) - rectDim / 2;
                        g.fill(point.color);
//                    g.rect(xp, yp, rectDim, rectDim);
                        g.set((int) (xp + rectDim / 2), (int) (yp + rectDim / 2), point.color);
                    //g.line(xp + rectDim / 2, yp + rectDim / 2, g.width / 2f, g.height / 2f);
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
