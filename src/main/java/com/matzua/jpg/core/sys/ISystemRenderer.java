package com.matzua.jpg.core.sys;

import processing.core.PGraphics;

public interface ISystemRenderer extends ProcessingAPI {
    void draw();
    PGraphics createGraphics(int width, int height);
}
