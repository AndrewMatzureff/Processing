package com.matzua.engine.event;

import lombok.*;

import java.lang.constant.Constable;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Builder (setterPrefix = "with")
@Getter
@AllArgsConstructor
public class Event {
    // category
    public static final int INPUT = 1;

    // device
    public static final int KEYBOARD = INPUT << 1;
    public static final int MOUSE = KEYBOARD << 1;
    public static final int JOYSTICK = MOUSE << 1;

    // interval
    public static final int DISCRETE = JOYSTICK << 1;
    public static final int CONTINUOUS = DISCRETE << 1;

    // range
    public static final int CLAMPED = CONTINUOUS << 1;
    public static final int PERIODIC = CLAMPED << 1;
    public static final int OSCILLATORY = PERIODIC << 1;

    // action
    public static final int STROKE = OSCILLATORY << 1;
    public static final int ENGAGED = STROKE << 1;
    public static final int DISENGAGED = ENGAGED << 1;

//    public interface Tags {
        public static final int UNDEFINED = 0;
        public static final int KEY_TYPED = INPUT | KEYBOARD | DISCRETE | CLAMPED | STROKE;
        public static final int KEY_PRESSED = INPUT | KEYBOARD | DISCRETE | CLAMPED | ENGAGED;
        public static final int KEY_RELEASED = INPUT | KEYBOARD | DISCRETE | CLAMPED | DISENGAGED;
        public static final int MOUSE_BUTTON_PRESSED = INPUT | MOUSE | DISCRETE | CLAMPED | ENGAGED;
        public static final int MOUSE_BUTTON_RELEASED = INPUT | MOUSE | DISCRETE | CLAMPED | DISENGAGED;
        public static final int MOUSE_MOVED = INPUT | MOUSE | DISCRETE | CLAMPED | STROKE;
        public static final int MOUSE_SCROLLED = INPUT | MOUSE | DISCRETE | CLAMPED | STROKE;
        public static final int LEFT_ANALOG_STICK_MOVED = INPUT | JOYSTICK | CONTINUOUS | CLAMPED | ENGAGED;
        public static final int LEFT_ANALOG_STICK_RESTORED = INPUT | JOYSTICK | CONTINUOUS | CLAMPED | DISENGAGED;
//    }

    public interface States {
//        public static final Supplier<Void> UNDEFINED = null;
//        public static final Supplier<Character> KEY_TYPED = STROKE;
//        public static final Supplier<Void> KEY_PRESSED = ENGAGED;
//        public static final Supplier<Void> KEY_RELEASED = DISENGAGED;
//        public static final Supplier<Void> MOUSE_BUTTON_PRESSED = ENGAGED;
//        public static final Supplier<Void> MOUSE_BUTTON_RELEASED = DISENGAGED;
//        public static final Supplier<Void> MOUSE_MOVED = STROKE;
//        public static final Supplier<Void> MOUSE_SCROLLED = STROKE;
//        public static final Supplier<Void> LEFT_ANALOG_STICK_MOVED = ENGAGED;
//        public static final Supplier<Void> LEFT_ANALOG_STICK_RESTORED = DISENGAGED;
    }

    int tags;
    Object state;

    public boolean is(int tags) {
        return (this.tags & tags) == tags;
    }
}
