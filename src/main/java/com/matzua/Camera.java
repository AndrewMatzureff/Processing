package com.matzua;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Camera {
    private static float Z_SCALE = 1000;
    float x, y, z, fov;

    public float project (float objectLength, float objectZ) {
        return objectLength / (objectZ - z) * Z_SCALE / (float) Math.tan(fov / 2);
    }

    public float projectX (float objectX, float objectZ, float displayWidth) {
        return (objectX - x) / (objectZ - z) * Z_SCALE / (float) Math.tan(fov / 2) + displayWidth / 2;
    }

    public float projectY (float objectY, float objectZ, float displayHeight) {
        return (objectY - y) / (objectZ - z) * Z_SCALE / (float) Math.tan(fov / 2) + displayHeight / 2;
    }
}
