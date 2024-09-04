package com.matzua.io;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class InputManager {
    public record KeyState(int key, boolean state) {
        public static KeyState pressed(int key) {
            return new KeyState(key, true);
        }

        public static KeyState released(int key) {
            return new KeyState(key, false);
        }
    }

    Map<Integer, String> keyBinds = new HashMap<>();

    // NOTE: secondary queue for uninterrupted key handling while primary is blocking?
    private final BlockingQueue<Character> chars = new LinkedBlockingQueue<>();
    Set<Integer> keys = new HashSet<>();

    public void tick() {
        final List<Character> currentChars = new LinkedList<>();
        chars.drainTo(currentChars);
        currentChars.forEach(c -> {});
    }

    public void handle(char key) {
        chars.add(key);
    }

    public void handle(KeyState keyState) {
        (keyState.state
            ? (Consumer<Integer>) (keys::add)
            : (Consumer<Integer>) (keys::remove))
            .accept(keyState.key);
    }

    public Stream<String> translate() {
        return keys.stream()
            .map(keyBinds::get)
            .filter(Objects::nonNull);
    }

    public void bind(Integer key, String actionId) {
        keyBinds.put(key, actionId);
    }

    public void unbind(Integer key) {
        keyBinds.remove(key);
    }
}
