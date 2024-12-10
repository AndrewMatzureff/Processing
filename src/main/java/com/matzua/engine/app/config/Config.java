package com.matzua.engine.app.config;

import com.matzua.engine.app.App;
import lombok.*;
import lombok.experimental.FieldDefaults;
import processing.core.PApplet;

import javax.inject.Inject;
import java.util.function.Function;

@Data
@Builder(setterPrefix = "with")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(onConstructor_ = {@Inject})
@AllArgsConstructor
public class Config {
    // Canvas
    @Builder.Default
    int canvasSizeHeight = 360;
    @Builder.Default
    int canvasSizeWidth = 640;
    // Window
    @Builder.Default
    String windowInfoTitle = null;
    @Builder.Default
    int windowSizeHeight = 360;
    @Builder.Default
    int windowSizeWidth = 640;
    public static String testWIT(Config c) {
        return c.getWindowInfoTitle();
    }
}
