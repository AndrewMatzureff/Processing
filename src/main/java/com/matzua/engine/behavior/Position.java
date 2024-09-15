package com.matzua.engine.behavior;

import com.matzua.engine.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import processing.core.PGraphics;

@Data
@AllArgsConstructor
public class Position implements Component {
    private final int owner;
    private float x, y, z;
    @Override
    public boolean onEvent(Event event) {
        return false;
    }

    @Override
    public void onRender(PGraphics g) {
    }

    @Override
    public void onUpdate() {
    }
}
