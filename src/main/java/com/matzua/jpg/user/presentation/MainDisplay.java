package com.matzua.jpg.user.presentation;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import com.matzua.jpg.core.app.resource.common.IAppStore;
import com.matzua.jpg.user.Component;
import lombok.RequiredArgsConstructor;
import processing.core.PGraphics;

@RequiredArgsConstructor
public class MainDisplay implements Component {
    private final PGraphicsCanvasStore canvasStore;
    private final String source;
    private final String target;

    @Override
    public void onUpdate() {
        final var target = canvasStore.get(this.target);
        final var source = canvasStore.get(this.source);
//        canvasStore.checkin(this.source);
        source.render(target);
    }

    @Override
    public void onRender(ICanvas canvas) {

    }

    @Override
    public void onEvent() {

    }
}
