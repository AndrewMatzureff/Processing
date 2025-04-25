package com.matzua.engine.renderer.rays;

import com.matzua.engine.component.input.InputController;
import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.Component;
import com.matzua.engine.entity.EntityManager;
import com.matzua.engine.event.Event;
import com.matzua.engine.renderer.rays.geom.RayCast;
import com.matzua.engine.renderer.rays.scene.CartesianGrid;
import com.matzua.engine.util.Initializer;
import com.matzua.engine.util.SequenceMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import processing.core.PConstants;
import processing.core.PGraphics;

import javax.inject.Inject;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static com.matzua.engine.util.Types.cast;
import static com.matzua.engine.util.Validation.ifAllPresent;

@Builder (setterPrefix = "with", buildMethodName = "defaultBuild")
@FieldDefaults (makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor (onConstructor_ = {@Inject})
public class RayCaster {
    UUID player;
    EntityManager entityManager;
    EventManager eventManager;
    @NonFinal
    PGraphics canvas;
    @NonFinal
    @Setter(value = AccessLevel.PRIVATE)
    Event.Camera camera;
    @NonFinal
    @Setter(value = AccessLevel.PRIVATE)
    CartesianGrid scene;
    @Builder.Default
    SequenceMap<Event.Render, /*Consumer<*/Event.Render/*>*/> operations = new SequenceMap.Impl<>(HashMap::new, LinkedList::new);
    @Builder.Default
    List<Event.Render> ops = new LinkedList<>();
    public void render(PGraphics canvas) {
        this.canvas = canvas;
        ifAllPresent(canvas, camera, scene).accept(() -> {
            canvas.beginDraw();
            canvas.push();
            canvas.background(0);
            synchronized (operations) {
                float cameraZ = (canvas.height / 2f) / (float) Math.tan(camera.fov() / 2f);
                canvas.perspective(camera.fov(), canvas.width / (float) canvas.height, cameraZ * 0.1f, cameraZ * 10f);
                canvas.translate(
                    canvas.width  / 2f - 0*camera.x(),
                    canvas.height / 2f - 0*camera.y(),
                    0*-camera.z()
                );
                //canvas.rotateX(PConstants.PI / 2);
                drawScene();

                // known
                float x = camera.x();
                float y = camera.y();
                float z = camera.z();
                float fov = camera.fov();
                float length = canvas.width;

                // derived

                // scene
                canvas.stroke(255);
                canvas.noFill();
                canvas.beginShape();
                canvas.vertex(-scene.scaledWidth() / 2, -scene.scaledHeight() / 2, 0);
                canvas.vertex(scene.scaledWidth() / 2, -scene.scaledHeight() / 2, 0);
                canvas.vertex(scene.scaledWidth() / 2, scene.scaledHeight() / 2, 0);
                canvas.vertex(-scene.scaledWidth() / 2, scene.scaledHeight() / 2, 0);
                canvas.endShape(PConstants.CLOSE);

                canvas.beginShape(PConstants.LINES);
                DoubleStream.iterate(
                        scene.scale(),
                        d -> d < scene.scaledWidth(),
                        d -> d + scene.scale()
                    )
                    .map(d -> d - scene.scaledWidth() / 2)
                    .forEach(d -> {
//                        canvas.vertex((float) d, scene.scaledHeight() / 2, 0);
//                        canvas.vertex((float) d, -scene.scaledHeight() / 2, 0);
                    });
                DoubleStream.iterate(
                        scene.scale(),
                        d -> d < scene.scaledHeight(),
                        d -> d + scene.scale()
                    )
                    .map(d -> d - scene.scaledHeight() / 2)
                    .forEach(d -> {
//                        canvas.vertex(scene.scaledWidth() / 2, (float) d, 0);
//                        canvas.vertex(-scene.scaledWidth() / 2, (float) d, 0);
                    });
//                float angle = fov / 2;
//                float o = length / 2;
//                float h = o / (float) Math.sin(angle);
//                float a = h * (float) Math.cos(angle);
//                canvas.vertex(camera.x(), camera.y(), camera.z()+cameraZ);
//                canvas.vertex(camera.x() - canvas.width / 2f, camera.y() - canvas.height / 2f, camera.z()+cameraZ);
//                canvas.vertex(camera.x(), camera.y(), camera.z()+cameraZ);
//                canvas.vertex(camera.x() + canvas.width / 2f, camera.y() - canvas.height / 2f, camera.z()+cameraZ);
//                canvas.vertex(camera.x(), camera.y(), camera.z()+cameraZ);
//                canvas.vertex(camera.x() - canvas.width / 2f, camera.y() + canvas.height / 2f, camera.z()+cameraZ);
//                canvas.vertex(camera.x(), camera.y(), camera.z()+cameraZ);
//                canvas.vertex(camera.x() + canvas.width / 2f, camera.y() + canvas.height / 2f, camera.z()+cameraZ);
                canvas.endShape();
            }
            canvas.pop();
            canvas.endDraw();
        });
    }

    private void drawScene() {
        final float[] mouse = new float[4];
        mouse[2] = 1;
        mouse[3] = 1;
        entityManager.message(Component.id(player, "main", InputController.class), inputController -> {
            Optional.ofNullable(inputController.getMouseContextEvent())
                .ifPresent(mouseContextEvent -> {
                    mouse[0] = mouseContextEvent.mouseX();// (float) mouseContextEvent.windowWidth() - 0.5f;
                    mouse[1] = mouseContextEvent.mouseY();// (float) mouseContextEvent.windowHeight() - 0.5f;
                    mouse[2] = mouseContextEvent.windowWidth();// (float) mouseContextEvent.windowWidth() - 0.5f;
                    mouse[3] = mouseContextEvent.windowHeight();// (float) mouseContextEvent.windowHeight() - 0.5f;
                });
        });
    final float mouseX = (mouse[0] / mouse[2] - 0.5f) * canvas.width;//DEBUG_MOUSE_X - canvas.width / 2f;
    final float mouseY = (mouse[1] / mouse[3] - 0.5f) * canvas.height;// - canvas.height / 2f;//DEBUG_MOUSE_Y - canvas.height / 2f;
        final UnaryOperator<Double> constraint = angle -> angle % PConstants.TWO_PI;
        final float viewX = mouseX - camera.x();
        final float viewY = mouseY - camera.y();
        final float viewL = (float) Math.sqrt(viewX * viewX + viewY * viewY);
        final double viewAngle = Math.acos(viewX / viewL);
        final double marchAngle = mouseY < camera.y() ? PConstants.TWO_PI - viewAngle : viewAngle;//(System.nanoTime() / 10000000000d * PConstants.TWO_PI));//.floatValue();
        System.out.printf("viewAngle=(%f), viewX=(%f), viewY=(%f)%n", viewAngle, viewX, viewY);
        DoubleStream.iterate(
                (marchAngle - PConstants.QUARTER_PI),
            angle -> angle <= marchAngle + PConstants.QUARTER_PI,
            angle -> angle + PConstants.HALF_PI / 32
        )
//        DoubleStream.of(-0.01)//marchAngle - PConstants.QUARTER_PI)
            .map(n -> n % PConstants.TWO_PI)
            .mapToObj(n -> (float) n)
            .forEach(angle -> cast(angle, camera.x(), camera.y()));

//        cast(marchAngle, camera.x(), camera.y());
//        cast((marchAngle + PConstants.HALF_PI) % PConstants.TWO_PI, camera.x(), camera.y());
    }

    private void cast(float marchAngle, float xInit, float yInit) {
        final float[] mouse = new float[4];
//        System.out.println("player: " + player);
        entityManager.message(Component.id(player, "main", InputController.class), inputController -> {
//            System.out.println("mouse: " + inputController.getMouseContextEvent());
            Optional.ofNullable(inputController.getMouseContextEvent())
                .ifPresent(mouseContextEvent -> {
                    mouse[0] = mouseContextEvent.mouseX();// (float) mouseContextEvent.windowWidth() - 0.5f;
                    mouse[1] = mouseContextEvent.mouseY();// (float) mouseContextEvent.windowHeight() - 0.5f;
                    mouse[2] = mouseContextEvent.windowWidth();// (float) mouseContextEvent.windowWidth() - 0.5f;
                    mouse[3] = mouseContextEvent.windowHeight();// (float) mouseContextEvent.windowHeight() - 0.5f;
                });
        });
        final float mouseX = (mouse[0] / mouse[2] - 0.5f) * canvas.width;//DEBUG_MOUSE_X - canvas.width / 2f;
        final float mouseY = (mouse[1] / mouse[3] - 0.5f) * canvas.height;// - canvas.height / 2f;//DEBUG_MOUSE_Y - canvas.height / 2f;
        // known
        final float fov = camera.fov();
        final float length = canvas.width;
        //final float marchAngle = (float) (Math.atan2(mouseX - x, mouseY - y) % PConstants.TWO_PI + PConstants.TWO_PI) % PConstants.TWO_PI;//((System.nanoTime() / 100000000000f * PConstants.TWO_PI) % PConstants.TWO_PI + PConstants.TWO_PI) % PConstants.TWO_PI;
        /*/ derived
        final float angle = fov / 2;
        final float o = length / 2;
        final float h = o / (float) Math.sin(angle);
        final float a = h * (float) Math.cos(angle);
        // unknown
        final float[] currentMarchPosition = {x, z};
        final int xCell = scene.columnAt(x);
        final int yCell = scene.rowAt(y);*/

        float xTerm = xInit;
        float yTerm = yInit;
        Optional<CartesianGrid.Step> maybeStep = scene.nextStep(marchAngle, xInit, yInit);
        canvas.push();
        while (maybeStep.isPresent()) {
            final CartesianGrid.Step step = maybeStep.get();
            final float x = step.x();// + step.xIncrement();
            final float y = step.y();// + step.yIncrement();
            final float z = camera.z();
            final int c = 0xff000000 |
                (step.edgeSeen() == 0 ? 0 : 255) << 16
                    |
                (step.edgeSeen() == 0 ? 125 : 0) << 8
                    |
                (step.edgeSeen() == 0 ? 125 : 0);
            xTerm = x + step.xHitOffset();
            yTerm = y + step.yHitOffset();
            //canvas.push();
            //canvas.translate(x, y, z);
            //canvas.rotateY(System.nanoTime() / 1000000000f * PConstants.PI * 2f);
            canvas.stroke(step.edgeSeen() == 0 ? 0 : 255, step.edgeSeen() == 0 ? 125 : 0, step.edgeSeen() == 0 ? 125 : 0);
//            if (step.edgeSeen() != 0 && Math.random() < 0.5) {
            canvas.push();
                canvas.strokeWeight(9);
                canvas.beginShape(PConstants.POINTS);
                canvas.vertex((step.x() + step.xHitOffset() - 0), (step.y() + step.yHitOffset() - 0), camera.z());//, 10, 10);
                canvas.endShape();
                canvas.pop();
//            }
            canvas.stroke(255);
            canvas.noFill();
//            canvas.beginShape(PConstants.LINES);
            ////canvas.vertex(x, y, z);
            //canvas.vertex(x+marchIncrement * (float) Math.cos(marchAngle), y+marchIncrement * (float) Math.sin(marchAngle), z);
//             canvas.vertex(x + step.xHitOffset(), y + step.yHitOffset(), z);
            ////canvas.vertex(x + step.xIncrement(), y + step.yIncrement(), z);
            // <increment corner>
//        canvas.vertex(x+step.xIncrement(), y, z);
//        canvas.vertex(x+step.xIncrement(), y+step.yIncrement(), z);
//        canvas.vertex(x, y+step.yIncrement(), z);
//        canvas.vertex(x+step.xIncrement(), y+step.yIncrement(), z);
            // </increment corner>
            // <quadrant axes>
//            canvas.vertex(x, y - (scene.yLocal(y) + scene.scale() / 2), z);
//            canvas.vertex(x, y + (scene.scale() / 2 - scene.yLocal(y)), z);
//            canvas.vertex(x - (scene.xLocal(x) + scene.scale() / 2), y, z);
//            canvas.vertex(x + (scene.scale() / 2 - scene.xLocal(x)), y, z);
            // </quadrant axes>
//            canvas.vertex(0, 0, z);
//            canvas.vertex(step.xHit(), step.yHit(), z);
            // <local to global>
//            canvas.vertex(0,0,z);
//            canvas.vertex(scene.xGlobal(scene.xLocal(step.x()), scene.columnAt(x)), scene.yGlobal(scene.yLocal(step.y()), scene.rowAt(y)), z);
            // </local to global>
//            canvas.endShape();
            canvas.color(255, 0, 0);
            canvas.textSize(48);
            canvas.text("(mouseX(w)=%f,mouseY(w)=%f)".formatted(mouse[0], mouse[1]), -500, 500);
            canvas.text("(mouseX(c)=%f,mouseY(c)=%f)".formatted(mouseX, mouseY), -500, 540);
//            canvas.text("(columnIndex=%d, rowIndex=%d)"
//                    .formatted(scene.columnToIndex(scene.columnAt(x)), scene.rowToIndex(scene.rowAt(y))),
//                scene.columnAt(x) * scene.scale(), scene.rowAt(y) * scene.scale());
//            canvas.text("(column=%d, row=%d)"
//                    .formatted((scene.columnAt(x)), (scene.rowAt(y))),
//                -scene.columnAt(x) * scene.scale(), scene.rowAt(y) * scene.scale() + scene.scale()/2);
//        canvas.text("(% 3d, % 3d)".formatted(xCell, yCell), xCell *scene.scale(), yCell *scene.scale(), z);
//        canvas.text("(% 1g, % 1g)".formatted(xLocal, yLocal), x + scene.scale(), y + scene.scale(), z);
//        canvas.text("[%f, %f]".formatted(marchIncrement * marchAngleAdjacent, marchIncrement * marchAngleOpposite), x*0.25f, y*0.25f, z);
//        canvas.text("cornerSlope=(%f/%f)=%f".formatted(cornerRise, cornerRun, cornerSlope), -500, -500, z);
//        canvas.text("marchSlope=%f".formatted(marchSlope), -500, 510, z);
//        canvas.text("edgeCrossed=%s".formatted(edgeCrossed), -500, 540, z);
            maybeStep = step.edgeSeen() != 0 ? Optional.empty() : scene.nextStep(marchAngle, x + step.xIncrement(), y + step.yIncrement());
        }
        canvas.beginShape(PConstants.LINES);
        canvas.vertex(xInit, yInit, camera.z());
        canvas.vertex(xTerm, yTerm, camera.z());
        canvas.vertex(xInit, yInit, camera.z());
        canvas.vertex(mouseX, mouseY);
        canvas.vertex(mouseX, mouseY);
        canvas.vertex((float) Math.cos(Math.PI / 4) * 0.01f * (xInit - mouseX), (float) -Math.sin(Math.PI / 4) * 0.01f * (yInit - mouseY));
        canvas.endShape();
        canvas.pop();
    }
    // a = 2arctan(1/e_z)
    // tan
    // b_x = e_z * d_x / d_z + e_x

    // b_x = d_x * s_x / (d_z * r_x) * r_z
    // b_y = d_y * s_y / (d_z * r_y) * r_z
    public void render(Event event) {
        Optional.of(event)
            .map(Event.Render.class::cast)
            ;//.ifPresent(render -> render.render(canvas));
    }
    private RayCaster init() {
        Stream.of(Event.Render.WirePath.class, Event.Render.Box.class, RayCast.Edge.RenderEvent.class)
            .forEach(type -> eventManager.subscribe(type, ops::add));

        eventManager.subscribe(CartesianGrid.class, this::setScene);
        eventManager.subscribe(Event.Camera.class, this::setCamera);
        return this;
    }
    public static class RayCasterBuilder implements Initializer<RayCaster> {
        public RayCaster build() {
            return init(RayCaster::init);
        }
    }
}
