package com.sonymobile.experienceflow.renderer;

import android.opengl.GLSurfaceView;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public final class ProxyFramerateController extends FramerateControllerBase {
    CurveRendererImpl curveRenderer;

    public ProxyFramerateController(int maxFps, GLSurfaceView surfaceView, CurveRendererImpl curveRenderer) {
        super(maxFps, surfaceView);
        this.curveRenderer = curveRenderer;
    }

    @Override // com.sonymobile.experienceflow.renderer.FramerateControllerBase
    protected int getFrameDelay(long deltaTime) {
        int t = this.curveRenderer.getSleepRecommendation() - ((int) deltaTime);
        return t;
    }
}
