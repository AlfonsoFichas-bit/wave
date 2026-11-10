package com.sonymobile.experienceflow.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class FileUtil {
    public static String readStringFromfile(String fileName) {
        String result = null;
        InputStream stream = null;
        ClassLoader cl = FileUtil.class.getClassLoader();
        if (cl == null) {
            return null;
        }
        try {
            try {
                stream = cl.getResourceAsStream(fileName);
                if (stream != null) {
                    int bytesRead = 0;
                    int bytesToRead = stream.available();
                    byte[] buffer = new byte[bytesToRead];
                    while (bytesRead < bytesToRead) {
                        int read = stream.read(buffer, bytesRead, bytesToRead - bytesRead);
                        if (read == -1) {
                            break;
                        }
                        bytesRead += read;
                    }
                    result = new String(buffer, Charset.forName("UTF-8"));
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w(Constants.TAG, "Couldn't close  " + fileName, e);
                    }
                }
            } catch (IOException e2) {
                Log.w(Constants.TAG, "Couldn't find " + fileName, e2);
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e3) {
                        Log.w(Constants.TAG, "Couldn't close  " + fileName, e3);
                    }
                }
            }
            return result;
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e4) {
                    Log.w(Constants.TAG, "Couldn't close  " + fileName, e4);
                }
            }
            throw th;
        }
    }

    public static Bitmap readBitmapFromfile(String fileName) {
        ClassLoader cl = FileUtil.class.getClassLoader();
        if (cl == null) {
            return null;
        }
        InputStream stream = cl.getResourceAsStream(fileName);
        return BitmapFactory.decodeStream(stream);
    }

    public static void closeStream(Closeable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.w(Constants.TAG, "Error closing stream.", e);
            }
        }
    }
}
