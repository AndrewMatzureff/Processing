package com.matzua.engine.app.simple;

import com.matzua.engine.app.AbstractApp;
import com.matzua.engine.app.interfaces.IEventManager;
import com.matzua.engine.app.interfaces.IGameLoop;
import com.matzua.engine.app.interfaces.IRenderer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import processing.event.KeyEvent;

@AllArgsConstructor
public class SimpleApp extends AbstractApp implements
    SimpleSystemInputEventDispatcher,
    SimpleEventLoopSystemRenderer {
    @Getter(onMethod_={@Override})
    private final IEventManager eventManager;
    @Getter(onMethod_={@Override})
    private final IGameLoop gameLoop;
    @Getter(onMethod_={@Override})
    private final IRenderer renderer;
    @Override
    public void keyPressed(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyPressed(event);}
    @Override
    public void keyReleased(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyReleased(event);}
    @Override
    public void keyTyped(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyTyped(event);}
}
