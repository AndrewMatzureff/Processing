package com.matzua.engine.app;

import com.matzua.engine.app.interfaces.IRenderer;
import com.matzua.engine.app.interfaces.ISystemInput;
import processing.core.PApplet;

public abstract class AbstractApp extends PApplet implements IRenderer, ISystemInput {
    public AbstractApp app() {return this;}
    public void settings() {
        size(320, 200);
    }
    public void draw() {
//        keyPressed(null);
        fill((int) System.nanoTime() | 0xff000000);
        rect(0,0,width,height);
    }
//    @Override
//    public void keyPressed(KeyEvent event) {super.keyPressed(event);}
//    void keyReleased(KeyEvent event);
//    void keyTyped(KeyEvent event);
//    public ISystemInput getSystemInput() {return this;}
//    public IRenderer getRenderer() {return this;}
}
