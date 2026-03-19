package com.matzua.engine.app.simple;

import com.matzua.engine.app.AbstractApp;
import com.matzua.engine.app.interfaces.IEventManager;
import processing.event.KeyEvent;

public class SimpleApp extends AbstractApp implements
    SimpleSystemInputEventDispatcher {
    private final IEventManager eventManager;
    public SimpleApp(IEventManager eventManager) {
        this.eventManager = eventManager;
    }
    @Override
    public IEventManager getEventManager() {
        return eventManager;
    }

    @Override
    public void keyPressed(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyPressed(event);}
    @Override
    public void keyReleased(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyReleased(event);}
    @Override
    public void keyTyped(KeyEvent event) {SimpleSystemInputEventDispatcher.super.keyTyped(event);}
}
