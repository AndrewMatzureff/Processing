package com.matzua.engine.renderer.geom;

import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.Component;
import com.matzua.engine.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Box implements Component {
    private final EventManager eventManager;
    float x, y, s;
    @Override
    public void onTick(Id<?> id) {
        eventManager.dispatch(new Event.Render.Box(x, y, s));
    }
}
