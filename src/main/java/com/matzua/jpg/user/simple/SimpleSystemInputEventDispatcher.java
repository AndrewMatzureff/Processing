package com.matzua.jpg.user.simple;

import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.sys.ISystemInput;
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
