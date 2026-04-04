package com.matzua.jpg.user.presentation;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.draw.IRenderer;
import com.matzua.jpg.core.app.resource.common.IAppStore;
import com.matzua.jpg.user.state.Entity;
import processing.core.PGraphics;

public interface ISimpleRenderer extends IRenderer {
    class X implements ISimpleRenderer {
        private final Entity entity;
        private final IAppStore<ICanvas> canvasStore;
        public X(Entity entity, IEventManager eventManager, IAppStore<ICanvas> canvasStore) {
            this.entity = entity;
            this.canvasStore = canvasStore;
//            attach(eventManager);
        }
        public void onRender(ICanvas canvas) {
            final PGraphics g = canvas.pGraphics();
            g.push();
            g   .stroke((int) System.nanoTime() | 0xff000000);
            g   .line(entity.x - 15, entity.y - 15, entity.x + 15, entity.y + 15);
            g   .line(entity.x + 15, entity.y - 15, entity.x - 15, entity.y + 15);
            g.pop();
        }
//        private void attach(IEventManager eventManager) {
//            eventManager.subscribe(Type.of(drawCommand.getClass()), iDraw -> render());
//        }
    }
    class Background implements ISimpleRenderer {
        private final Entity entity;
        private final IAppStore<ICanvas> canvasStore;
        public Background(Entity entity, IEventManager eventManager, IAppStore<ICanvas> canvasStore) {
            this.entity = entity;
            this.canvasStore = canvasStore;
//            attach(eventManager);
        }
        public void onRender(ICanvas canvas) {
            final PGraphics g = canvas.pGraphics();
            g.background((int) System.currentTimeMillis() / 10 | 0xff000000);
//            g.fill((int) System.currentTimeMillis() / 10 | 0xff000000);
//            g.rect(0,0,g.width,g.height);
        }
//        private void attach(IEventManager eventManager) {
//            eventManager.subscribe(Type.of(drawCommand.getClass()), iDraw -> render());
//        }
    }
}
