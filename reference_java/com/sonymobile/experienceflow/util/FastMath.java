package com.sonymobile.experienceflow.util;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public final class FastMath {
    private FastMath() {
    }

    public static short convertFloatToHalf(float flt) {
        if (Float.isNaN(flt)) {
            throw new UnsupportedOperationException("NaN to half conversion not supported!");
        }
        if (flt == Float.POSITIVE_INFINITY) {
            return (short) 31744;
        }
        if (flt == Float.NEGATIVE_INFINITY) {
            return (short) -1024;
        }
        if (flt == 0.0f) {
            return (short) 0;
        }
        if (flt == -0.0f) {
            return Short.MIN_VALUE;
        }
        if (flt > 65504.0f) {
            return (short) 31743;
        }
        if (flt < -65504.0f) {
            return (short) -1025;
        }
        if (flt > 0.0f && flt < 5.96046E-8f) {
            return (short) 1;
        }
        if (flt < 0.0f && flt > -5.96046E-8f) {
            return (short) -32767;
        }
        int f = Float.floatToIntBits(flt);
        return (short) (((f >> 16) & 32768) | ((((2139095040 & f) - 939524096) >> 13) & 31744) | ((f >> 13) & 1023));
    }
}
