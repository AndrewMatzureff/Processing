package com.matzua.jpg.user.simple;

import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsIngredients;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsRecipe;
import com.matzua.jpg.core.app.resource.common.AbstractResourceStore;
import com.matzua.jpg.core.sys.AbstractApp;
import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.IGameState;
import com.matzua.jpg.core.app.draw.IRenderer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import processing.event.KeyEvent;

import javax.inject.Inject;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SimpleApp extends AbstractApp implements
    SimpleSystemInputEventDispatcher,
    SimpleEventLoopSystemRenderer {
    @Getter(onMethod_={@Override})
    private final AbstractResourceStore canvasStore;
    @Getter(onMethod_={@Override})
    private final IEventManager eventManager;
    @Getter(onMethod_={@Override})
    private final IGameState gameState;
    @Getter(onMethod_={@Override})
    private final IRenderer renderer;
    // ↓ Plug in system input module ↓ \\...............................................................................
    @Override
    public void keyPressed(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyPressed(event);}
    @Override
    public void keyReleased(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyReleased(event);}
    @Override
    public void keyTyped(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyTyped(event);}
    // ↓ Plug in system renderer module ↓ \.............................................................................
    @Override
    public void draw() {SimpleEventLoopSystemRenderer.super.draw();}
    // ↓ Plug in system settings module ↓ \.............................................................................
    @Override
    public void settings() {
        size(1920, 1080);
    }
    // ↓ Plug in system setup module ↓ \................................................................................
    @Override
    public void setup() {
        canvasStore.root("root", this);
        canvasStore.create("main", canvasStore.getCanvasFactory(
            PGraphicsRecipe.from(this),
            PGraphicsIngredients.from(320, 200)
        ));
    }
    // ↓ Misc. ↓ \......................................................................................................git
}
