package com.matzua.sample.jpg.simple.dev;

import com.matzua.jpg.user.simple.SimpleApp;
//import com.matzua.game.example.dagger.component.DaggerAppComponent;
import com.matzua.jpg.user.simple.SimpleEventManager;
import com.matzua.jpg.user.state.Type;
import com.matzua.sample.jpg.simple.dagger.component.AppComponent;
import com.matzua.sample.jpg.simple.dagger.component.DaggerAppComponent;
import processing.core.PApplet;
import processing.event.KeyEvent;

import java.util.HashMap;

public class Dev {
    public static void main(String[] args) {
        final AppComponent component = DaggerAppComponent.create();
        final SimpleApp app = component.getSimpleApp();//new SimpleApp(null, new SimpleEventManager(new HashMap<>()), null, null);//component.getApp();
        app.getEventManager().subscribe(Type.of(KeyEvent.class), System.out::println);
        PApplet.runSketch(new String[] {"Dev"}, app);
    }
}
