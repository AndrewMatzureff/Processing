package com.matzua.engine.component.scene;

import com.matzua.engine.entity.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Position implements Component {
    private float x, y, s;
    @Override
    public void onTick(Id<?> id) {}
}
