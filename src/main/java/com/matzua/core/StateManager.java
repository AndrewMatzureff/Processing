package com.matzua.core;

import com.matzua.Camera;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class StateManager {
    Map<String, Camera> cameras = new HashMap<>();
    Map<String, Consumer<Camera>> cameraMutators = new HashMap<>();
    Map<String, Float> floats = new HashMap<>();
    Map<Class<?>, Map<String, Object>> untypedState = new HashMap<>();
    // NOTE: store ALL mutable state as primitives within StateManager and then allow access to game objects in order to "sync" their mutable fields each frame?

    public <T> void put(String id, T state) {
        untypedState.merge(state.getClass(), new HashMap<>(Map.of(id, state)), (oldValue, value) -> {
            oldValue.putAll(value);
            return oldValue;
        });
    }

    public void put(String id, Camera camera) {
        cameras.put(id, camera);
    }

    public void put(String id, Consumer<Camera> mutator) {
        cameraMutators.put(id, mutator);
    }

    public Set<Camera> getCameras(String id) {
        return Set.of(cameras.get(id));
    }

    public Set<Consumer<Camera>> getCameraMutators(String id) {
        return Set.of(cameraMutators.get(id));
    }
}
