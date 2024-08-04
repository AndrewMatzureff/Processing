package com.matzua;

import processing.core.PApplet;
import processing.event.KeyEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main extends PApplet {
    int lastMouseX, lastMouseY;
    List<Cloud> clouds;
    Camera camera;
    Set<Character> pressedKeys;
    Set<Integer> pressedCodes;
    public void settings() {
        size (640, 480, P2D);
        keyRepeatEnabled = false;
        lastMouseX = width / 2;
        lastMouseY = height / 2;

        clouds = List.of(
                Cloud.of(Cloud.sphere(height, 0, 0, 25000, 1), true),
                Cloud.of(Cloud.fill(height, 0, 0, 25000, 9999, this::random, this::random), false)
        );

        camera = new Camera(0,0,20000, (float) Math.toRadians(175));
        pressedKeys = new HashSet<>();
        pressedCodes = new HashSet<>();
        pressedKeys.add('w');
    }

    public void setup() {
        windowTitle("movement: 'W', 'A', 'S', 'D', 'C', 'SPACE', 'SHIFT'; field of view: '-', '='");
    }

    public void draw() {
        float ds = pressedCodes.contains(SHIFT) ? 25 : 1;
        float df = (pressedKeys.contains('-') ? -(float) Math.PI / 64 : 0) + (pressedKeys.contains('=') ? (float) Math.PI / 64 : 0);
        float dx = (pressedKeys.contains('a') ? -10 : 0) + (pressedKeys.contains('d') ? 10 : 0);
        float dy = (pressedKeys.contains(' ') ? -10 : 0) + (pressedKeys.contains('c') ? 10 : 0);
        float dz = (pressedKeys.contains('s') ? -10 : 0) + (pressedKeys.contains('w') ? 10 : 0);

        camera.fov = camera.fov + df < 0 ? 0 : camera.fov + df > Math.PI ? (float) Math.PI : camera.fov + df;
        camera.x += dx * ds;
        camera.y += dy * ds;
        camera.z += dz * ds;

        int start = 24000;
        int end = 25000;
        if (camera.z > end) camera.z = start;
        //else if (camera.z < start) camera.z = end;

        background (0);
        stroke (0);
        line (lastMouseX, lastMouseY, mouseX, mouseY);
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        clouds.forEach(cloud -> cloud.draw(this, camera));

        text("FOV: %d".formatted((int) Math.toDegrees(camera.fov)), 10, 10);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKey() == CODED) {
            pressedCodes.add(keyEvent.getKeyCode());
        } else {
            pressedKeys.add(Character.toLowerCase(keyEvent.getKey()));
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent.getKey() == CODED) {
            pressedCodes.remove(keyEvent.getKeyCode());
        } else {
            pressedKeys.remove(Character.toLowerCase(keyEvent.getKey()));
        }
    }

    public static void main (String[] args) {
        PApplet.main (Main.class.getName ());
    }
}