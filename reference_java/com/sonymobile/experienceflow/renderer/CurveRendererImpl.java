package com.sonymobile.experienceflow.renderer;

import android.content.res.AssetManager;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class CurveRendererImpl extends CurveRenderer {
    int id;
    protected RenderListener renderListener;

    /* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
    public interface RenderListener {
        void pleaseRender();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void pleaseRender() {
        if (this.renderListener != null) {
            synchronized (this) {
                this.renderListener.pleaseRender();
            }
        }
    }

    public CurveRendererImpl(AssetManager am) {
        this.id = init(am);
    }

    public synchronized void updateScreenLayout(int x, int y, int w, int h, int sw, int sh) {
        super.updateScreenLayout(this.id, x, y, w, h, sw, sh);
    }

    public synchronized void requestColors(AssetManager am, int theme) {
        super.requestColors(this.id, am, theme);
        pleaseRender();
    }

    public synchronized void touchDown(float x, float y) {
        super.touchDown(this.id, x, y);
        pleaseRender();
    }

    public synchronized void touch(float x, float y) {
        super.touch(this.id, x, y);
        pleaseRender();
    }

    public synchronized void touchUp(float x, float y) {
        super.touchUp(this.id, x, y);
        pleaseRender();
    }

    public synchronized void activate() {
        super.activate(this.id);
        pleaseRender();
    }

    public synchronized void deactivate() {
        super.deactivate(this.id);
        pleaseRender();
    }

    public synchronized void setOnRenderListener(RenderListener l) {
        this.renderListener = l;
    }

    public synchronized void render(AssetManager am) {
        super.render(this.id, am);
    }

    public synchronized int getSleepRecommendation() {
        return super.getSleepRecommendation(this.id);
    }
}
