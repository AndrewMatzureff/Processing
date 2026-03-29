package com.matzua.jpg.core.app.resource.canvas;

import com.matzua.jpg.core.sys.AbstractApp;
import processing.core.PGraphics;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface PGraphicsIngredients {
    int width();
    int height();
    static PGraphicsIngredients from(int width, int height) {
        return new PGraphicsIngredients() {
            @Override public int width() {return width;}
            @Override public int height() {return height;}
        };
    }
    static PGraphicsIngredients from(int dimension) {return from(dimension, dimension);}
}
