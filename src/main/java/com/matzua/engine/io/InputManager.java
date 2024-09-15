package com.matzua.engine.io;

import lombok.NoArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Singleton
@NoArgsConstructor(onConstructor = @__(@Inject))
public class InputManager {
    public record DigitalAxisState(int axis, boolean state) {
        public static DigitalAxisState high(int axis) {
            return new DigitalAxisState(axis, true);
        }

        public static DigitalAxisState low(int axis) {
            return new DigitalAxisState(axis, false);
        }

        public static DigitalAxisState ground(int axis) {
            return low(axis);
        }
    }
    public record ContinuousAxisState(int axis, double state) {
        public static ContinuousAxisState of(int axis, double state) {
            return new ContinuousAxisState(axis, state);
        }
        public static ContinuousAxisState high(int axis) {
            return new ContinuousAxisState(axis, 1);
        }

        public static ContinuousAxisState low(int axis) {
            return new ContinuousAxisState(axis, -1);
        }

        public static ContinuousAxisState ground(int axis) {
            return new ContinuousAxisState(axis, 0);
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

    public void handle(char keyStroke) {
        chars.add(keyStroke);
    }

    public void handle(DigitalAxisState axisState) {
        (axisState.state
            ? (Consumer<Integer>) (keys::add)
            : (Consumer<Integer>) (keys::remove))
            .accept(axisState.axis);
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
