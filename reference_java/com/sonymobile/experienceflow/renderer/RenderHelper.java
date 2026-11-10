package com.sonymobile.experienceflow.renderer;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class RenderHelper {
    CurveRendererImpl curveRenderer;
    private volatile Thread thread;

    public RenderHelper(CurveRendererImpl cri) {
        this.curveRenderer = cri;
    }

    public void continueFrame() {
        this.thread.interrupt();
    }

    public void startRenderTimingLoop() {
        if (this.thread == null) {
            this.thread = new Thread(new Runnable() { // from class: com.sonymobile.experienceflow.renderer.RenderHelper.1
                @Override // java.lang.Runnable
                public void run() {
                    RenderHelper.this.curveRenderer.activate();
                    while (RenderHelper.this.thread != null) {
                        RenderHelper.this.curveRenderer.pleaseRender();
                        try {
                            Thread.sleep(RenderHelper.this.curveRenderer.getSleepRecommendation());
                        } catch (InterruptedException e) {
                        }
                    }
                    RenderHelper.this.curveRenderer.deactivate();
                }
            });
            this.thread.start();
        }
    }

    public void stopRenderTimingLoop() {
        if (this.thread != null) {
            Thread t = this.thread;
            this.thread = null;
            t.interrupt();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
