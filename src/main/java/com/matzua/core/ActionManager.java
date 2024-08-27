package com.matzua.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.stream.Stream;

public class ActionManager {
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Batch {
        private final ActionManager context;
        private final Stream<String> actionIds;
        public Batch with(Stream<String> actionIds) {
            return new Batch(context, Stream.concat(this.actionIds, actionIds));
        }

        public void execute(StateManager stateManager) {
            context.execute(stateManager,actionIds);
        }
    }

    public Batch batch() {
        return new Batch(this, Stream.empty());
    }

    public void execute(StateManager stateManager, Stream<String> actionIds) {
        actionIds.forEach(id -> stateManager.getCameras(id)
            .forEach(camera -> stateManager.getCameraMutators(id)
                .forEach(mutator -> mutator.accept(camera))));
    }
}
