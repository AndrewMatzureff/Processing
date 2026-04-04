package com.matzua.jpg.user;

import com.matzua.jpg.core.app.ICanvas;

public interface Component {
    void onUpdate();
    void onRender(ICanvas canvas);
    void onEvent();
}
