package com.matzua;

import com.matzua.core.ActionManager;
import com.matzua.core.StateManager;
import com.matzua.io.InputManager;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.opengl.PGraphicsOpenGL;

import java.util.*;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public class Main extends PApplet {
    InputManager inputManager = new InputManager();
    ActionManager actionManager = new ActionManager();
    StateManager stateManager = new StateManager();
    Map<Integer, String> keyBinds = new HashMap<>();
    PGraphics pg;
    int lastMouseX, lastMouseY;
    List<Cloud> clouds;
    Camera camera;
    Set<Character> pressedKeys;
    Set<Integer> pressedCodes;
    public void settings() {
        size (640, 360, P2D);
    }

    public void setup() {
        windowTitle("movement: 'W', 'A', 'S', 'D', 'C', 'SPACE', 'SHIFT'; field of view: '-', '='");
        pg = createGraphics(320, 180, P3D);

        if (pg.isGL()) {
            ((PGraphicsOpenGL) pg).textureSampling(2);
        }

        if (g.isGL()) {
            ((PGraphicsOpenGL) g).textureSampling(2);
        }

        keyRepeatEnabled = false;
        lastMouseX = width / 2;
        lastMouseY = height / 2;

        clouds = List.of(
            Cloud.of(Stream.of(
                new Cloud.Point(0, height, 0, 0xffffffff),
                new Cloud.Point(0, -height, 0, 0xffffffff)
            ), true),
            Cloud.of(Stream.of(
                new Cloud.Point(height, 0, 0, 0xffffffff),
                new Cloud.Point(-height, 0, 0, 0xffffffff)
            ), true),
            Cloud.of(Stream.of(
                new Cloud.Point(0, 0, height, 0xffffffff),
                new Cloud.Point(0, 0, -height, 0xffffffff)
            ), true),
                Cloud.of(Cloud.sphere(height, 0, 0, 0*25000, 1), true),
                Cloud.of(Cloud.fill(height, 0, 0, 0*25000, 9999, this::random, this::random), false)
        );

        camera = new Camera(0,0,0, (float) Math.toRadians(175));
        pressedKeys = new HashSet<>();
        pressedCodes = new HashSet<>();
//        pressedKeys.add('w');
//        eventRegistrar.register("player.move.backward", (boolean key) -> key ? );
        keyBinds.put((int) 'W', "forward");
        keyBinds.put((int) 'S', "backward");
        keyBinds.put((int) 'A', "left");
        keyBinds.put((int) 'D', "right");
        stateManager.put("forward", camera);
        stateManager.put("backward", camera);
        stateManager.put("left", camera);
        stateManager.put("right", camera);
        stateManager.put("forward", camera -> camera.z -= 10);
        stateManager.put("backward", camera -> camera.z += 10);
        stateManager.put("left", camera -> camera.x -= 10);
        stateManager.put("right", camera -> camera.x += 10);
    }

    public void draw() {
        actionManager.batch()
            .with(inputManager.translate(keyBinds))
            .execute(stateManager);

        push();
//        float ds = pressedCodes.contains(SHIFT) ? 25 : 1;
//        float df = (pressedKeys.contains('-') || pressedKeys.contains('_') ? -(float) Math.PI / 64 : 0)
//                 + (pressedKeys.contains('=') || pressedKeys.contains('+') ? +(float) Math.PI / 64 : 0);
//        float dx = (pressedKeys.contains('a') ? -10 : 0) + (pressedKeys.contains('d') ? 10 : 0);
//        float dy = (pressedKeys.contains(' ') ? -10 : 0) + (pressedKeys.contains('c') ? 10 : 0);
//        float dz = (pressedKeys.contains('s') ? 10 : 0) + (pressedKeys.contains('w') ? -10 : 0);
//
//        camera.fov = camera.fov + df < 0 ? 0 : camera.fov + df > Math.PI ? (float) Math.PI : camera.fov + df;
//        camera.x += dx * ds;
//        camera.y += dy * ds;
//        camera.z += dz * ds;

        int start = 24000;
        int end = 25000;
//        if (camera.z > end) camera.z = start;
        //else if (camera.z < start) camera.z = end;

        pg.beginDraw();
        pg.push();
        pg.background (0);
        pg.stroke(255);
//        pg.camera();
//        pg.perspective();
        camera.fov = PI / 2f;
        pg.perspective(
                camera.vfov(pg.width, pg.height),
                (float) pg.width / pg.height,
                1,//cameraZ / 10 - camera.z,
                10000//cameraZ * 1000 - camera.z
        );
        pg.translate(-camera.x + pg.width / 2f, -camera.y + pg.height / 2f, -camera.z);

        pg.beginShape(PConstants.LINE);
        pg.vertex(0, 0, 0);
        pg.vertex(0, 0, -25000);
        pg.endShape();
//        pg.line(0, 0, 0, 1, 1, -25000);

        clouds.forEach(cloud -> cloud.draw(pg));
        //pg.camera();
        pg.pop();
        pg.endDraw();

        image(pg, 0, 0, width, height);

        stroke (0);
        line (lastMouseX, lastMouseY, mouseX, mouseY);
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        text("FOV: %d".formatted((int) Math.toDegrees(camera.fov)), 10, 10);
        text("(x=%9f, y=%9f, z=%9f)".formatted(camera.x, camera.y, camera.z), 10, 20);
        pop();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        Optional.of(keyEvent)
            .map(KeyEvent::getKey)
            .filter(not(Character.valueOf((char) CODED)::equals))
            .ifPresent(inputManager::handle);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        Optional.of(keyEvent)
            .map(KeyEvent::getKeyCode)
            .map(InputManager.KeyState::pressed)
            .ifPresent(inputManager::handle);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        Optional.of(keyEvent)
            .map(KeyEvent::getKeyCode)
            .map(InputManager.KeyState::released)
            .ifPresent(inputManager::handle);
    }

    public static void main (String[] args) {
        PApplet.main (Main.class.getName ());
    }
}