package com.matzua.engine.entity;

import com.matzua.engine.core.EventManager;
import com.matzua.engine.util.Validation;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Builder (setterPrefix = "with")
@FieldDefaults (makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor(onConstructor_ = {@Inject})
public class EntityManager {
    @AllArgsConstructor
    public static class Entity {
        private final EventManager eventManager;
        private final Map<Component.Id<?>, Component> components;
        public void tick() {
            components.values().forEach(Component::onTick);
        }
        public <T extends Component> void attach(String descriptor, T component) {
            components.put(new Component.Id<>(descriptor, component.getClass()), component);
        }
        public <T extends Component> void message(Component.Id<T> recipient, Consumer<T> message) {
            Optional.of(recipient)
                .map(components::get)
                .ifPresent(c -> c.onMessage(message));
        }
    }
    EventManager eventManager;
    List<Entity> entities;

    public void tick() {
        entities.forEach(Entity::tick);
    }

    public Entity getEntity(int id) {
        // TODO: replace entities list with map.
        return entities.stream()
            .filter(entity -> entity.hashCode() == id)
            .findAny()
            .orElseThrow(Validation::newPlaceholderError);
    }

    public int addEntity(Entity entity) {
        // TODO: revise id handling after separating out Entity class from EntityManager.
        entities.add(entity);
        return entity.hashCode();
    }
}
