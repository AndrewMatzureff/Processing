package com.matzua.jpg.core.app;

import processing.core.PGraphics;

public interface ICanvas {
    // TODO: remove pGraphics() and implement a specific, narrow set of PGraphics operations.
    PGraphics pGraphics();
    default void render(ICanvas target) {
        target.pGraphics().image(this.pGraphics(), 0, 0, target.pGraphics().width, target.pGraphics().height);
    }
}
