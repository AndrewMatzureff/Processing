package com.matzua.jpg.user.state;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.resource.AbstractResourceStore;
import com.matzua.jpg.core.app.resource.IFreeAccessResource;
import com.matzua.jpg.user.Component;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class Entity implements IFreeAccessResource {
    @ToString.Exclude
    public final List<Component> components = new ArrayList<>();
    private final AbstractResourceStore<ICanvas> canvasStore;
    private final IEventManager eventManager;
    public float x = 0, y = 0;
    public Entity(AbstractResourceStore<ICanvas> canvasStore, IEventManager eventManager) {
        this.canvasStore = canvasStore;
        this.eventManager = eventManager;
    }
    public void update() {
        components.forEach(Component::onUpdate);
    }
}
