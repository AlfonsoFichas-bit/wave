package com.sonymobile.experienceflow.renderer;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public abstract class FramerateControllerBase implements FramerateController {
    private static final long INITIAL_MAX_TIME_BETWEEN_RENDER_REQUESTS = 10;
    private static final long RENDER_DELAY_REQUESTS_MULTIPLICATOR = 10;
    private GLSurfaceView mGLSurfaceView;
    private int mMaxFps;
    private long mMaxTimeBetweenRenderRequest = 10;
    private long mStartTime = 0;
    private boolean mCapFrameRate = true;
    private final RenderRequest mRenderRequest = new RenderRequest();
    private volatile int mRendering = 0;
    private AtomicBoolean mForceSingleFrame = new AtomicBoolean(false);
    private ArrayList<RenderModeListener> mRenderModeListeners = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    protected abstract int getFrameDelay(long j);

    public FramerateControllerBase(int maxFps, GLSurfaceView surfaceView) {
        this.mMaxFps = maxFps;
        setSurfaceView(surfaceView);
    }

    @Override // com.sonymobile.experienceflow.renderer.FramerateController
    public int getMaxFps() {
        return this.mMaxFps;
    }

    @Override // com.sonymobile.experienceflow.renderer.FramerateController
    public void setMaxFps(int maxFps) {
        this.mMaxFps = maxFps;
    }

    @Override // com.sonymobile.experienceflow.renderer.FramerateController
    public long onFrame() {
        this.mHandler.removeCallbacks(this.mRenderRequest);
        long endTime = SystemClock.elapsedRealtime();
        long deltaTime = endTime - this.mStartTime;
        long delay = getFrameDelay(deltaTime);
        long baseDelay = (1000 / this.mMaxFps) - deltaTime;
        if (baseDelay > delay) {
            delay = baseDelay;
        }
        if (delay >= 1) {
            long actualDelay = delay;
            if (this.mMaxTimeBetweenRenderRequest < actualDelay) {
                actualDelay = this.mMaxTimeBetweenRenderRequest;
                this.mMaxTimeBetweenRenderRequest *= 10;
            }
            this.mHandler.postDelayed(this.mRenderRequest, actualDelay);
        } else {
            this.mHandler.post(this.mRenderRequest);
        }
        return deltaTime;
    }

    @Override // com.sonymobile.experienceflow.renderer.FramerateController
    public void continueFrame() {
        this.mHandler.removeCallbacks(this.mRenderRequest);
        long delay = (1000 / this.mMaxFps) - (SystemClock.elapsedRealtime() - this.mStartTime);
        if (this.mCapFrameRate && delay > 1.0d) {
            this.mHandler.postDelayed(this.mRenderRequest, delay);
        } else {
            this.mRenderRequest.run();
        }
    }

    @Override // com.sonymobile.experienceflow.renderer.FramerateController
    public void setCapFrameRate(boolean cap) {
        this.mCapFrameRate = cap;
    }

    @Override // com.sonymobile.experienceflow.renderer.FramerateController
    public boolean getCapFrameRate() {
        return this.mCapFrameRate;
    }

    @Override // com.sonymobile.experienceflow.renderer.FramerateController
    public void setSurfaceView(GLSurfaceView surfaceView) {
        this.mGLSurfaceView = surfaceView;
        this.mHandler.removeCallbacks(this.mRenderRequest);
        this.mHandler.post(this.mRenderRequest);
    }

    @Override // com.sonymobile.experienceflow.renderer.FramerateController
    public void startRendering() {
        synchronized (this) {
            if (this.mRendering < 0) {
                this.mRendering = 0;
            }
            int i = this.mRendering;
            this.mRendering = i + 1;
            if (i == 0) {
                synchronized (this.mRenderModeListeners) {
                    Iterator<RenderModeListener> it = this.mRenderModeListeners.iterator();
                    while (it.hasNext()) {
                        RenderModeListener rml = it.next();
                        rml.onStartRendering();
                    }
                }
                this.mHandler.removeCallbacks(this.mRenderRequest);
                this.mHandler.post(this.mRenderRequest);
                this.mMaxTimeBetweenRenderRequest = 10L;
            }
        }
    }

    @Override // com.sonymobile.experienceflow.renderer.FramerateController
    public void stopRendering() {
        synchronized (this) {
            int i = this.mRendering - 1;
            this.mRendering = i;
            if (i != 0) {
                if (this.mRendering < 0) {
                    this.mRendering = 0;
                }
                return;
            }
            synchronized (this.mRenderModeListeners) {
                Iterator<RenderModeListener> it = this.mRenderModeListeners.iterator();
                while (it.hasNext()) {
                    RenderModeListener rml = it.next();
                    rml.onStopRendering();
                }
            }
            this.mHandler.removeCallbacks(this.mRenderRequest);
        }
    }

    public void forceSingleFrameRender() {
        this.mForceSingleFrame.set(true);
        this.mHandler.removeCallbacks(this.mRenderRequest);
        this.mHandler.postAtFrontOfQueue(this.mRenderRequest);
    }

    public void addRenderModeListener(RenderModeListener renderModeListener) {
        synchronized (this.mRenderModeListeners) {
            this.mRenderModeListeners.add(renderModeListener);
        }
    }

    public void removeRenderModeListener(RenderModeListener renderModeListener) {
        synchronized (this.mRenderModeListeners) {
            this.mRenderModeListeners.remove(renderModeListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
    public final class RenderRequest implements Runnable {
        private RenderRequest() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (FramerateControllerBase.this.mGLSurfaceView != null) {
                if (FramerateControllerBase.this.mRendering > 0 || FramerateControllerBase.this.mForceSingleFrame.getAndSet(false)) {
                    FramerateControllerBase.this.mStartTime = SystemClock.elapsedRealtime();
                    FramerateControllerBase.this.mGLSurfaceView.requestRender();
                }
            }
        }
    }
}
