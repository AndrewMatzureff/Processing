package com.matzua.engine.renderer.rays.scene;

import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.Component;
import com.matzua.engine.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import processing.core.PConstants;

import java.util.Map;
import java.util.Optional;

public record CartesianGrid(float scale, int width, int height, int[][] cells,
                            EventManager eventManager) implements Component, Event {
    public static int EAST_VISIBLE = 1;
    public static int SOUTH_VISIBLE = 2;
    public static int WEST_VISIBLE = 4;
    public static int NORTH_VISIBLE = 8;
    public float scaledWidth() {
        return width() * scale();
    }
    public float scaledHeight() {
        return height() * scale();
    }
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Orientation {
        EAST(0), SOUTH_EAST(1), WEST(9);
        private final int index;
    }
    public record Hit(float xLocal, float yLocal) {}
    public record Step(//Should Step store origin + increment instead of target?
                       float angle,
                       float x,
                       float y,
                       float increment,
                       float xIncrement,
                       float yIncrement,
                       float xHitOffset,
                       float yHitOffset,
                       int edgeSeen
    ) {}

    private int toIndex(int cell, int length) {
//        final int halfLength = length / 2;
//        final int index = cell + halfLength;
//        return ((-(index / halfLength) >> 31) & ((length << 31) >> 31)) + index;
        return length % 2 == 0
            ? 0
            : cell + length / 2;
    }

    public int columnToIndex(int column) {
        return toIndex(column, width());
    }

    public int rowToIndex(int row) {
        return toIndex(row, height());
    }

    private float coterminal(float angle) {
        final float roundingPrecision = 1000f;
        final float coterminal = Math.round(angle * roundingPrecision) / roundingPrecision % PConstants.TWO_PI;
        return coterminal + (coterminal < 0f ? PConstants.TWO_PI : 0f);
    }

    public Optional<Step> nextStep(float angle, float x, float y) {
        final float coterminal = coterminal(angle);
        // known
//        final float x = camera.x();
//        final float y = camera.y();
//        final float z = camera.z();
//        final float fov = camera.fov();
//        final float length = canvas.width;
        // derived
//        final float angle = fov / 2;
//        final float o = length / 2;
//        final float h = o / (float) Math.sin(angle);
//        final float a = h * (float) Math.cos(angle);
        // unknown
//        final float[] currentMarchPosition = {x, z};
        final int xCell = this.columnAt(x);
        final int yCell = this.rowAt(y);
        final int columnIndex = columnToIndex(xCell);
        final int rowIndex = rowToIndex(yCell);
        if ((columnIndex | rowIndex) < 0 || columnIndex >= width() || rowIndex >= height()) {
            return Optional.empty();
        }
        final float xLocal = this.xLocal(x);
        final float yLocal = this.yLocal(y);
        //final float marchAngle = (float) (Math.atan2(mouseX - x, mouseY - y) % PConstants.TWO_PI + PConstants.TWO_PI) % PConstants.TWO_PI;//((System.nanoTime() / 100000000000f * PConstants.TWO_PI) % PConstants.TWO_PI + PConstants.TWO_PI) % PConstants.TWO_PI;
        final float marchAngleAdjacent = (float) Math.cos(coterminal);
        final float marchAngleOpposite = (float) Math.sin(coterminal);
        final float xCorner = coterminal < PConstants.PI / 2 * 3 && coterminal >= PConstants.PI / 2
            ? -this.scale() / 2// - xlocal
            : this.scale() / 2;// - xlocal;
        final float yCorner = coterminal < PConstants.PI
            ? this.scale() / 2// - ylocal
            : -this.scale() / 2;// - ylocal;
        final float cornerRise = (yCorner - yLocal);
        final float cornerRun = (xCorner - xLocal);
        final float marchIncrement = (float) Math.sqrt(cornerRun * cornerRun + cornerRise * cornerRise);
        final float xIncrement = marchIncrement * marchAngleAdjacent;
        final float yIncrement = marchIncrement * marchAngleOpposite;
        final float cornerSlope = cornerRise / cornerRun;
        final float marchSlope = (marchAngleOpposite) / (marchAngleAdjacent);
        final int quadrantLocal = (int) ((coterminal / (Math.PI * 2d)) * 4d);
        String edgeCrossed;
        try {
            edgeCrossed = new String[]
                {"E0", "S1", "S2", "W3", "W4", "N5", "N6", "E7"}[
                // After nearly 2 hours of runtime: "java.lang.ArrayIndexOutOfBoundsException: Index 9 out of bounds for length 8".
                quadrantLocal * 2 + (marchSlope >= cornerSlope ? 1 : 0)
//            camera: viewAngle=(0.785398), viewX=(55.999985), viewY=(-55.999996)
//            java.lang.RuntimeException: quadrantLocal:4 * 2 + (marchSlope:0.000000 >= cornerSlope:-1.000000 ? 1 : 0):1
//            2q + 0 = 9
//            2q = 9
//            q = 4
//
//            2q + 1 = 9
//            2q = 8
//            q = 4
                ];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException(
                "coterminal:%f, quadrantLocal:%d * 2 + (marchSlope:%f >= cornerSlope:%f ? 1 : 0):%d"
                    .formatted(coterminal, quadrantLocal, marchSlope, cornerSlope, (marchSlope >= cornerSlope ? 1 : 0))
            );
        }
        final float interceptAxisBound;
        final Map<String, Float> edgeInterceptScalars = Map.of(
            "E0", 1f - (xLocal + marchIncrement * marchAngleAdjacent - this.scale() / 2) / (marchIncrement * marchAngleAdjacent),
            "S1", 1f - (yLocal + marchIncrement * marchAngleOpposite - this.scale() / 2) / (marchIncrement * marchAngleOpposite),
            "S2", 1f - (yLocal + marchIncrement * marchAngleOpposite - this.scale() / 2) / (marchIncrement * marchAngleOpposite),
            "W3", 1f - (xLocal + marchIncrement * marchAngleAdjacent + this.scale() / 2) / (marchIncrement * marchAngleAdjacent),
            "W4", 1f - (xLocal + marchIncrement * marchAngleAdjacent + this.scale() / 2) / (marchIncrement * marchAngleAdjacent),
            "N5", 1f - (yLocal + marchIncrement * marchAngleOpposite + this.scale() / 2) / (marchIncrement * marchAngleOpposite),
            "N6", 1f - (yLocal + marchIncrement * marchAngleOpposite + this.scale() / 2) / (marchIncrement * marchAngleOpposite),
            "E7", 1f - (xLocal + marchIncrement * marchAngleAdjacent - this.scale() / 2) / (marchIncrement * marchAngleAdjacent)
        );
        final int visibilityByEdgeInitial = switch (edgeCrossed.charAt(0)) {
            case 'E' -> EAST_VISIBLE;
            case 'S' -> SOUTH_VISIBLE;
            case 'W' -> WEST_VISIBLE;
            case 'N' -> NORTH_VISIBLE;
            default -> throw new IllegalStateException("Unexpected value: " + edgeCrossed.charAt(0));
        };
        return Optional.of(
            new Step(
                coterminal,
                x,
                y,
                marchIncrement,
                xIncrement,
                yIncrement,
                edgeInterceptScalars.get(edgeCrossed) * marchAngleAdjacent * marchIncrement,
                edgeInterceptScalars.get(edgeCrossed) * marchAngleOpposite * marchIncrement,
                visibilityByEdgeInitial & cells()[columnIndex][rowIndex]
            )
        );
    }

    private float global(float n, int c, int d) {
        final float g = c * scale() + n;
        return d % 2 == 0
            ? g - Math.signum(g) * scale() / 2
            : g;
    }

    public float xGlobal(float x, int c) {
        return global(x, c, width);
    }

    public float yGlobal(float y, int r) {
        return global(y, r, height);
    }

    private float local(float n, int d) {
        final float l = n % scale() - scale() / 2 * Math.signum(n);
        return d % 2 == 0
            ? l
            : scale() / 2 * Math.signum(-l) - (scale() / 2 * Math.signum(n) - n % scale());
    }

    public float xLocal(float x) {
        return local(x, width);
    }

    public float yLocal(float y) {
        return local(y, height);
    }

    private int at(float n, int d) {
        return d % 2 == 0
            ? (int) (Math.ceil(Math.abs(n) / scale()) * Math.signum(n))// * scale()
            : Math.round(n / scale());// * scale();
    }

    public int columnAt(float x) {
        return at(x, width());
    }

    public int rowAt(float y) {
        return at(y, height());
    }

    @Override
    public void onTick(Id<?> id) {
        eventManager.dispatch(this);
    }
}
