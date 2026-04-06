package com.matzua.jpg.user.presentation;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import com.matzua.jpg.user.Component;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FullscreenDisplay implements Component {
    private final PGraphicsCanvasStore canvasStore;
    private final String source;
    private final String target;

    @Override
    public void onUpdate() {
        try (var target = canvasStore.get(this.target);
             var source = canvasStore.get(this.source)) {
            source.render(target);
        }
    }

    @Override
    public void onRender(ICanvas canvas) {

    }

    @Override
    public void onEvent() {

    }
}
