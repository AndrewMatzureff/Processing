package com.matzua.engine.renderer.geom;

import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.Component;
import com.matzua.engine.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class Box implements Component {
    EventManager eventManager;
    float x, y, s;
    @Override
    public void onTick() {
        eventManager.dispatch(new Event.Render.Box(x, y, s));
    }
}
