package com.matzua.jpg.user.simple;

import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import com.matzua.jpg.core.sys.AbstractApp;
import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.IGameLoop;
import com.matzua.jpg.core.app.IRenderer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import processing.event.KeyEvent;

import javax.inject.Inject;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SimpleApp extends AbstractApp implements
    SimpleSystemInputEventDispatcher,
    SimpleEventLoopSystemRenderer {
    @Getter(onMethod_={@Override})
    private final PGraphicsCanvasStore canvasStore;
    @Getter(onMethod_={@Override})
    private final IEventManager eventManager;
    @Getter(onMethod_={@Override})
    private final IGameLoop gameLoop;
    @Getter(onMethod_={@Override})
    private final IRenderer renderer;
    // ↓ Plug in system input module ↓ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    @Override
    public void
    keyPressed(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyPressed(event);}
    @Override
    public void
    keyReleased(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyReleased(event);}
    @Override
    public void
    keyTyped(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyTyped(event);}
    // ↓ Plug in system renderer module ↓ \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    @Override
    public void draw() {SimpleEventLoopSystemRenderer.super.draw();}
    // ↓ Plug in system settings module ↓ \\
    @Override
    public void settings() {
        size(320, 200);
    }
    // ↓ Plug in system setup module ↓ \\
    @Override
    public void setup() {
    }
    // ↓ Misc. ↓ \\

//    fill((int) System.nanoTime() | 0xff000000);
//    rect(0,0,width,height);
}
