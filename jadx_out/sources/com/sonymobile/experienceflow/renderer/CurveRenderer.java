package com.sonymobile.experienceflow.renderer;

import android.content.res.AssetManager;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public abstract class CurveRenderer {
    /* JADX INFO: Access modifiers changed from: protected */
    public native synchronized void activate(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public native synchronized void deactivate(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public native synchronized int getSleepRecommendation(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public native synchronized int init(AssetManager assetManager);

    /* JADX INFO: Access modifiers changed from: protected */
    public native synchronized void render(int i, AssetManager assetManager);

    /* JADX INFO: Access modifiers changed from: protected */
    public native synchronized void requestColors(int i, AssetManager assetManager, int i2);

    /* JADX INFO: Access modifiers changed from: protected */
    public native synchronized void touch(int i, float f, float f2);

    /* JADX INFO: Access modifiers changed from: protected */
    public native synchronized void touchDown(int i, float f, float f2);

    /* JADX INFO: Access modifiers changed from: protected */
    public native synchronized void touchUp(int i, float f, float f2);

    /* JADX INFO: Access modifiers changed from: protected */
    public native synchronized void updateScreenLayout(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    static {
        System.loadLibrary("lwplocal");
    }
}
