package com.matzua.engine.entity;

import com.matzua.engine.core.EventManager;
import lombok.*;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;

@Builder (setterPrefix = "with")
@AllArgsConstructor(onConstructor_ = {@Inject})
public class EntityManager {
    private final EventManager eventManager;
    private final Map<Component.Id<?>, Component> components;
    public <T extends Component> EntityManager attach(UUID entity, String descriptor, T component) {
        components.put(component.id(entity, descriptor), component);
        return this;
    }
    public <T extends Component> void message(Component.Id<T> recipient, Consumer<T> message) {
        Optional.of(recipient)
            .map(components::get)
            .ifPresent(c -> c.onMessage(message));
    }
    public void tick() {
        components.forEach(Component::tick);
    }
    public Map<Component.Id<?>, Component> getComponents() {
        return Collections.unmodifiableMap(components);
    }
}
