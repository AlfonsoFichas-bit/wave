package com.sonymobile.experienceflow.renderer;

import android.opengl.GLSurfaceView;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public interface FramerateController {
    void continueFrame();

    boolean getCapFrameRate();

    int getMaxFps();

    long onFrame();

    void setCapFrameRate(boolean z);

    void setMaxFps(int i);

    void setSurfaceView(GLSurfaceView gLSurfaceView);

    void startRendering();

    void stopRendering();
}
