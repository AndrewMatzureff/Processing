package com.matzua.engine.core;

import com.matzua.engine.Camera;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import static processing.core.PConstants.P3D;
import static processing.core.PConstants.PI;

public class StateManager {
    private final BlockingQueue<String> activeMessages = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> pendingMessages = new LinkedBlockingQueue<>();
    Map<String, Camera> cameras = new HashMap<>();
    Map<String, PGraphics> graphics = new HashMap<>();
    Map<String, Float> floats = new HashMap<>();
    Map<String, Integer> ints = new HashMap<>();
    Map<String, String> strings = new HashMap<>();
    // NOTE: store ALL mutable state as primitives within StateManager and then allow access to game objects in order to "sync" their mutable fields each frame?

    public void update() {
        final List<String> visitedMessages = new LinkedList<>();
        activeMessages.drainTo(visitedMessages);
        visitedMessages.forEach(m -> {});
        pendingMessages.drainTo(activeMessages);
    }

    public void send(String message) {
        pendingMessages.add(message);
    }

    public void registerString(String resourceId, String resource) {
        register(resourceId, resource, strings);
    }

    public void registerGraphics(String resourceId, int width, int height, PApplet app) {
        register(resourceId, app.createGraphics(width, height, P3D), graphics);
    }

    public void registerCamera(String resourceId) {
        register(resourceId, new Camera(0, 0, 0, PI), cameras);
    }

    public Optional<PGraphics> getGraphics(String resourceId) {
        return get(resourceId, graphics);
    }

    public Optional<Camera> getCamera(String resourceId) {
        return get(resourceId, cameras);
    }

    public Optional<String> getString(String resourceId) {
        return get(resourceId, strings);
    }

    public static <T> Optional<T> get(String resourceId, Map<String, T> registry) {
        return Optional.of(resourceId).map(registry::get);
    }

    private static <T> void register(String resourceId, T resource, Map<String, T> registry) {
        if (registry.containsKey(resourceId)) {
            throw new IllegalArgumentException("TODO: Update this error message.");
        }

        registry.put(resourceId, resource);
    }

    public void put(String resourceId, Camera camera) {
        cameras.put(resourceId, camera);
    }

    public void remove(String resourceId) {
        Stream.of(cameras, graphics, floats, ints).forEach(map -> map.remove(resourceId));
    }
}
