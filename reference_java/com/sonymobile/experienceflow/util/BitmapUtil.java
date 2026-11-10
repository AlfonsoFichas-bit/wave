package com.sonymobile.experienceflow.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class BitmapUtil {
    private static final BitmapFactory.Options sUnscaledOption = new BitmapFactory.Options();

    static {
        sUnscaledOption.inScaled = false;
    }

    public static Bitmap getUnscaledBitmap(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId, sUnscaledOption);
    }

    public static Bitmap create(int color) {
        Bitmap bitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(color);
        return bitmap;
    }

    public static BitmapFactory.Options getOption(InputStream is, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return options;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2;
            }
            long totalReqPixelsCap = 2 * reqWidth * reqHeight;
            for (long totalPixels = (width * height) / inSampleSize; totalPixels > totalReqPixelsCap; totalPixels /= 2) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
