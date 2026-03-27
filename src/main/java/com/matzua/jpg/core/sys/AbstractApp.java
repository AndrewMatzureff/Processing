package com.matzua.jpg.core.sys;

import processing.core.PApplet;

public abstract class AbstractApp extends PApplet implements
    ISystemRenderer, ISystemInput, ISystemSettings, ISystemSetup {
    public AbstractApp app() {return this;}
}
