package com.matzua.game.example.dev;

import com.matzua.engine.app.App;
import com.matzua.game.example.dagger.component.AppComponent;
import com.matzua.game.example.dagger.component.DaggerAppComponent;
import processing.core.PApplet;

public class Dev {
    public static void main(String[] args) {
        final AppComponent component = DaggerAppComponent.create();
        final App app = component.getApp();
        PApplet.runSketch(new String[] {"Dev"}, app);
    }
}
