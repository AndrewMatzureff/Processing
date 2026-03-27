package com.matzua.jpg.core.app.resource.canvas;

import com.matzua.jpg.core.sys.AbstractApp;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface PGraphicsRecipe extends Supplier<BiFunction<Integer, Integer, PGraphics>> {
    static PGraphicsRecipe from(AbstractApp app) {
        return () -> app::createGraphics;
    }
}
