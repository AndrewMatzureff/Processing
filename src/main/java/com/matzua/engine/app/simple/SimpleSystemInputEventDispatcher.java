package com.matzua.engine.app.simple;

import com.matzua.engine.app.interfaces.IEventManager;
import com.matzua.engine.app.interfaces.ISystemInput;
import processing.event.KeyEvent;

public interface SimpleSystemInputEventDispatcher extends ISystemInput {
    IEventManager getEventManager();
    @Override
    default void keyPressed(KeyEvent event) {System.out.println("keyPressed");getEventManager().dispatch(event);}
    @Override
    default void keyReleased(KeyEvent event) {
        getEventManager().dispatch(event);
    }
    @Override
    default void keyTyped(KeyEvent event) {
        getEventManager().dispatch(event);
    }
}
