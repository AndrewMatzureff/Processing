package com.matzua;

import com.matzua.dagger.component.AppComponent;
import com.matzua.dagger.component.DaggerAppComponent;
import processing.core.PApplet;

public class Main {
    public static void main (String[] args) {
        final AppComponent component = DaggerAppComponent.create();
        PApplet.runSketch(new String[] {"Sketch"}, component.getApp());
    }
}
