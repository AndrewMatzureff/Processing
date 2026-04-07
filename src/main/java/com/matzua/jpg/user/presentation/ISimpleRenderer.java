package com.matzua.jpg.user.presentation;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.IRenderer;
import com.matzua.jpg.user.state.Entity;
import processing.core.PGraphics;

public interface ISimpleRenderer extends IRenderer {
    record X(Entity entity, String channel) implements ISimpleRenderer {
        public void onRender(ICanvas canvas) {
            final PGraphics g = canvas.pGraphics();
            g.push();
            g   .stroke((int) System.nanoTime() | 0xff000000);
            g   .line(entity.x - 15, entity.y - 15, entity.x + 15, entity.y + 15);
            g   .line(entity.x + 15, entity.y - 15, entity.x - 15, entity.y + 15);
            g.pop();
        }
    }
    record Background(String channel) implements ISimpleRenderer {
        public void onRender(ICanvas canvas) {
            final PGraphics g = canvas.pGraphics();
            g.background((int) System.currentTimeMillis() / 10 | 0xff000000);
        }
    }
}
