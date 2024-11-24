package com.matzua.engine.app;

import com.matzua.engine.core.EventManager;
import com.matzua.engine.core.LayerManager;
import lombok.AllArgsConstructor;
import processing.core.PApplet;

import javax.inject.Inject;

@AllArgsConstructor(onConstructor_ = {@Inject})
public class App extends PApplet {
    private final EventManager eventManager;
    private final LayerManager layerManager;
}
