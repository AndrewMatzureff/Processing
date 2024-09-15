package com.matzua.client;

import com.matzua.client.WorldLayer;
import com.matzua.client.dagger.component.AppComponent;
import com.matzua.client.dagger.component.DaggerAppComponent;
import com.matzua.client.dagger.module.EntityModule;
import com.matzua.client.dagger.module.LayerModule;
import com.matzua.engine.App;
import com.matzua.engine.core.EntityManager;
import processing.core.PApplet;

public class Main {
    public static void main (String[] args) {
        final AppComponent component = DaggerAppComponent.create();
        final App app = component.getApp();
        final EntityManager entityManager = EntityModule.provideEntityManager(); // For now till I decide on final structure...
        LayerModule.provideLayers(entityManager).forEach(app::pushLayer);
        PApplet.runSketch(new String[] {"Sketch"}, app);
    }
}
