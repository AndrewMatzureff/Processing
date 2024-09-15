package com.matzua.engine.core;

import com.matzua.engine.behavior.Component;
import com.matzua.engine.event.Event;
import com.matzua.util.FunctionalUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.function.*;

public class EntityManager {
    @Inject
    public EntityManager() {}
    public interface ComponentGetter<T extends Component> extends Supplier<Map.Entry<Class<T>, EntityManager>> {
        default Optional<T> from(int entityId) {
            final Map.Entry<Class<T>, EntityManager> context = get();
            return context.getValue().getComponentFromEntity(context.getKey(), entityId);
        }

        default void forEach(Consumer<T> action, Layer layer) {
            final Map.Entry<Class<T>, EntityManager> context = get();
            context.getValue().componentsByEntity.keySet().stream()
                .map(id -> context.getValue().getComponentFromEntity(context.getKey(), id))
                .map(r -> {System.out.println(r); return r;})
                .filter(Optional::isPresent)
                .map(Optional::orElseThrow)
                .filter(layer::contains)
                .forEach(action);
        }
    }
    public interface ComponentAttacher<T extends Component> extends Supplier<Map.Entry<T, EntityManager>> {
        default void to(int entityId) {
            final Map.Entry<T, EntityManager> context = get();
            context.getValue().attachComponentToEntity(context.getKey(), entityId);
        }
    }

    private int size = 0;

    Map<Integer, Map<Class<?>, Component>> componentsByEntity = new HashMap<>();

    public boolean dispatch(Event event) {
        return componentsByEntity.values()
            .stream()
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(FunctionalUtils.biFunction(Component::onEvent).bind(event))
            .reduce(Boolean::logicalOr)
            .orElse(false);
    }

    public void create(int entityId, Component...components) {
        Arrays.stream(components)
            .forEach(component -> attachComponentToEntity(component, entityId));
    }

    public <T extends Component> ComponentGetter<T> get(Class<T> componentType) {
        return () -> Map.entry(componentType, this);
    }

    private <T> Optional<T> getComponentFromEntity(Class<T> componentType, int entityId) {
        Map<Class<?>, ?> components = Optional.of(entityId)
            .map(componentsByEntity::get)
            .orElseThrow(() -> new IllegalArgumentException(
                "Tried to obtain the %s component from a nonexistent entity with id=%d."
                    .formatted(componentType.getSimpleName(), entityId)));
        System.out.println("[getComponentFromEntity] " + components);

        return Optional.of(componentType)
            .map(components::get)
            .map(componentType::cast);
    }

    private void attachComponentToEntity(Component component, int entityId) {
        componentsByEntity.merge(entityId, new HashMap<>(Map.of(component.getClass(), component)), this::putAll);
    }

    private Map<Class<?>, Component> putAll(Map<Class<?>, Component> oldMap, Map<Class<?>, Component> newMap) {
        // TODO: ensure that attempts to overwrite an existing component throw an error instead
        oldMap.putAll(newMap);
        return oldMap;
    }
}
