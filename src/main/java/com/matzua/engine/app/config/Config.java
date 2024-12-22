package com.matzua.engine.app.config;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.inject.Inject;

import static processing.core.PConstants.P2D;

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
    @Builder.Default
    String windowRenderer = P2D;
}
