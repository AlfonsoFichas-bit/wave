package com.sonymobile.experienceflow.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class BufferUtil {
    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_SHORT = 2;

    public static FloatBuffer createFloatBuffer(float[] floats) {
        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(floats.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(floats).position(0);
        return floatBuffer;
    }

    public static ShortBuffer createShortBuffer(float[] floats) {
        ShortBuffer shortBuffer = ByteBuffer.allocateDirect(floats.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        for (float f : floats) {
            shortBuffer.put(FastMath.convertFloatToHalf(f));
        }
        shortBuffer.position(0);
        return shortBuffer;
    }
}
