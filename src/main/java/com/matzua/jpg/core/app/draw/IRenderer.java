package com.matzua.jpg.core.app.draw;

import com.matzua.jpg.user.Component;

public interface IRenderer extends Component {
    default void onEvent() {}
    default void onUpdate() {}
    default String channel() {return "root";}
}
