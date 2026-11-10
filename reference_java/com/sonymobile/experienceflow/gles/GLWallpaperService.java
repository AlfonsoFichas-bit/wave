package com.sonymobile.experienceflow.gles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public abstract class GLWallpaperService extends WallpaperService {

    /* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
    public abstract class GLEngine extends WallpaperService.Engine {
        protected WallpaperGLSurfaceView mGLSurfaceView;
        private GLSurfaceView.Renderer mRenderer;

        public abstract WallpaperGLSurfaceView createGLSurfaceView();

        public GLEngine() {
            super(GLWallpaperService.this);
            setTouchEventsEnabled(true);
        }

        @Override // android.service.wallpaper.WallpaperService.Engine
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.mGLSurfaceView = createGLSurfaceView();
        }

        @Override // android.service.wallpaper.WallpaperService.Engine
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (this.mGLSurfaceView != null && this.mRenderer != null) {
                if (visible) {
                    this.mGLSurfaceView.onResume();
                } else {
                    this.mGLSurfaceView.onPause();
                }
            }
        }

        @Override // android.service.wallpaper.WallpaperService.Engine
        public void onDestroy() {
            super.onDestroy();
            if (this.mGLSurfaceView != null) {
                this.mGLSurfaceView.onDestroy();
            }
        }

        protected void setRenderer(GLSurfaceView.Renderer renderer) {
            this.mRenderer = renderer;
            if (this.mGLSurfaceView != null) {
                this.mGLSurfaceView.setRenderer(renderer);
                this.mGLSurfaceView.onResume();
            }
        }

        protected GLSurfaceView.Renderer getRenderer() {
            return this.mRenderer;
        }

        /* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
        public abstract class WallpaperGLSurfaceView extends GLSurfaceView {
            protected GLSurfaceView.Renderer mRenderer;

            public abstract GLSurfaceView.Renderer createRenderer(Context context, GLSurfaceView gLSurfaceView);

            public WallpaperGLSurfaceView(Context context) {
                super(context);
                setEGLContextClientVersion(2);
                setEGLConfigChooser(8, 8, 8, 0, 0, 0);
                getHolder().setFormat(3);
                setPreserveEGLContextOnPause(true);
                this.mRenderer = createRenderer(GLWallpaperService.this.getBaseContext(), this);
                setRenderer(this.mRenderer);
                setRenderMode(0);
            }

            @Override // android.view.SurfaceView
            public SurfaceHolder getHolder() {
                return GLEngine.this.getSurfaceHolder();
            }

            public void onDestroy() {
                super.onDetachedFromWindow();
            }
        }
    }
}
