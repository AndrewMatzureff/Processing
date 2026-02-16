package com.matzua.game.example.dev;

import com.matzua.engine.app.SimpleApp;
//import com.matzua.game.example.dagger.component.DaggerAppComponent;
import com.matzua.engine.app.SimpleEventManager;
import processing.core.PApplet;
import processing.event.KeyEvent;

import java.util.HashMap;

public class Dev {
    public static void main(String[] args) {
//        final AppComponent component = DaggerAppComponent.create();
        final SimpleApp app = new SimpleApp(new SimpleEventManager(new HashMap<>()));//component.getApp();
        app.getEventManager().subscribe(KeyEvent.class, System.out::println);
        PApplet.runSketch(new String[] {"Dev"}, app);
    }
}
