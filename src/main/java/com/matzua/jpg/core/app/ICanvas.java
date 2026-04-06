package com.matzua.jpg.core.app;

import processing.core.PGraphics;

public interface ICanvas extends IControlledAccessResource {
    // TODO: remove pGraphics() and implement a specific, narrow set of PGraphics operations.
    PGraphics pGraphics();
    default void render(ICanvas target) {
        target
            .pGraphics()
            .image(pGraphics(), 0, 0, target.pGraphics().width, target.pGraphics().height);
    }
    default void open() {pGraphics().beginDraw();}
    default void close() {pGraphics().endDraw();}
}
