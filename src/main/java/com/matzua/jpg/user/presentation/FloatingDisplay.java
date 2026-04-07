package com.matzua.jpg.user.presentation;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.user.Component;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class FloatingDisplay implements Component {
    private final String source;
    private final String target;
    public float x = 0, y = 0;

    @Override
    public String channel() {return target;}

    @Override
    public void onRender(ICanvas canvas) {}

    @Override
    public void postRender(ICanvas canvas, Map<String, ICanvas> canvasesByChannel) {
        canvas.pGraphics().image(canvasesByChannel.get(source).pGraphics(), x, y);
    }
}
