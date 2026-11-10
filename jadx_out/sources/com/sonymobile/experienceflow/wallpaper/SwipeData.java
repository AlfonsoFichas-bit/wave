package com.sonymobile.experienceflow.wallpaper;

import com.sonymobile.experienceflow.renderer.ExperienceFlowRenderer;
import com.sonymobile.experienceflow.util.FreeList;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class SwipeData implements Runnable {
    private boolean mAction;
    private FreeList<SwipeData> mFreeList;
    private ExperienceFlowRenderer mRenderer;
    private float mX;

    public SwipeData setFreeList(FreeList<SwipeData> freeList) {
        this.mFreeList = freeList;
        return this;
    }

    public SwipeData setRenderer(ExperienceFlowRenderer renderer) {
        this.mRenderer = renderer;
        return this;
    }

    public SwipeData setX(float x) {
        this.mX = x;
        return this;
    }

    public SwipeData setActionUp(boolean action) {
        this.mAction = action;
        return this;
    }

    public float getX() {
        return this.mX;
    }

    public boolean getActionUp() {
        return this.mAction;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.mRenderer.handleSwipe(this);
        this.mFreeList.checkIn(this);
    }
}
