package com.matzua.engine.entity;

import com.matzua.engine.core.EventManager;
import com.matzua.engine.event.Event;
import com.matzua.engine.util.Validation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Builder (setterPrefix = "with")
@FieldDefaults (makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor(onConstructor_ = {@Inject})
public class EntityManager {
    @Data
    @AllArgsConstructor
    public static class Entity {
        private float x, y;
        private final EventManager eventManager;

        public void tick() {
            eventManager.dispatch(new Event.Render.Box(x, y, 25));
        }
    }
    EventManager eventManager;
    List<Entity> entities;

    public void tick() {
        entities.forEach(Entity::tick);
    }

    public Entity getEntity(int id) {
        return Optional.of(id)
            .map(entities::get)
            .orElseThrow(Validation::newPlaceholderError);
    }

    public int addEntity(Entity entity) {
        final int id = entities.size();
        entities.add(entity);
        return id;
    }
}
