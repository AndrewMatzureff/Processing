package com.matzua.jpg.core.sys;

import processing.event.KeyEvent;

public interface ISystemInput extends ProcessingAPI {
    void keyPressed(KeyEvent event);
    void keyReleased(KeyEvent event);
    void keyTyped(KeyEvent event);
}
