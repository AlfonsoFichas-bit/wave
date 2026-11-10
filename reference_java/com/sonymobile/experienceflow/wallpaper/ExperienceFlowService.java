package com.sonymobile.experienceflow.wallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.sonymobile.experienceflow.gles.GLWallpaperService;
import com.sonymobile.experienceflow.renderer.ExperienceFlowRenderer;
import com.sonymobile.experienceflow.util.Constants;
import com.sonymobile.experienceflow.util.FreeList;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class ExperienceFlowService extends GLWallpaperService {
    @Override // android.service.wallpaper.WallpaperService, android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    @Override // android.service.wallpaper.WallpaperService, android.app.Service
    public void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override // android.service.wallpaper.WallpaperService
    public WallpaperService.Engine onCreateEngine() {
        return new GLWallpaperService.GLEngine() { // from class: com.sonymobile.experienceflow.wallpaper.ExperienceFlowService.1
            private ExperienceFlowGLSurfaceView mView;

            @Override // com.sonymobile.experienceflow.gles.GLWallpaperService.GLEngine, android.service.wallpaper.WallpaperService.Engine
            public void onCreate(SurfaceHolder surfaceHolder) {
                super.onCreate(surfaceHolder);
                this.mView = (ExperienceFlowGLSurfaceView) this.mGLSurfaceView;
            }

            @Override // com.sonymobile.experienceflow.gles.GLWallpaperService.GLEngine, android.service.wallpaper.WallpaperService.Engine
            public void onDestroy() {
                super.onDestroy();
            }

            @Override // android.service.wallpaper.WallpaperService.Engine
            public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
                if (!LockscreenApiConstants.LOCKSCREEN_WALLPAPER_COMMAND_POINTER_DOWN.equals(action)) {
                    if (!LockscreenApiConstants.LOCKSCREEN_WALLPAPER_COMMAND_POINTER_MOVE.equals(action)) {
                        if (!LockscreenApiConstants.LOCKSCREEN_WALLPAPER_COMMAND_POINTER_UP.equals(action)) {
                            if (LockscreenApiConstants.LOCKSCREEN_WALLPAPER_COMMAND_ACCELERATE.equals(action) && Math.abs(x) <= 10000) {
                                this.mView.accelerate(x);
                            }
                        } else {
                            this.mView.mRenderer.touchUp(x, y);
                        }
                    } else {
                        this.mView.mRenderer.touch(x, y);
                    }
                } else {
                    this.mView.mRenderer.touchDown(x, y);
                }
                return super.onCommand(action, x, y, z, extras, resultRequested);
            }

            @Override // android.service.wallpaper.WallpaperService.Engine
            public void onTouchEvent(MotionEvent event) {
                super.onTouchEvent(event);
                this.mView.handleTouchEvent(event);
                switch (event.getAction()) {
                    case 0:
                        this.mView.mRenderer.touchDown(event.getX(), event.getY());
                        return;
                    case 1:
                        this.mView.mRenderer.touchUp(event.getX(), event.getY());
                        return;
                    case 2:
                        this.mView.mRenderer.touch(event.getX(), event.getY());
                        return;
                    case 3:
                    default:
                        return;
                }
            }

            @Override // com.sonymobile.experienceflow.gles.GLWallpaperService.GLEngine
            public GLWallpaperService.GLEngine.WallpaperGLSurfaceView createGLSurfaceView() {
                return new ExperienceFlowGLSurfaceView(ExperienceFlowService.this);
            }

            @Override // com.sonymobile.experienceflow.gles.GLWallpaperService.GLEngine, android.service.wallpaper.WallpaperService.Engine
            public void onVisibilityChanged(boolean visible) {
                super.onVisibilityChanged(visible);
                if (visible) {
                    this.mView.startRendering();
                } else {
                    this.mView.stopRendering();
                }
            }

            /* renamed from: com.sonymobile.experienceflow.wallpaper.ExperienceFlowService$1$ExperienceFlowGLSurfaceView */
            /* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
            class ExperienceFlowGLSurfaceView extends GLWallpaperService.GLEngine.WallpaperGLSurfaceView implements Refreshable {
                private SwipeData mDelayedEvent;
                private FreeList<SwipeData> mFreeList;
                private ExperienceFlowRenderer mRenderer;

                public ExperienceFlowGLSurfaceView(Context context) {
                    super(context);
                    this.mFreeList = new FreeList<>(SwipeData.class, 50);
                }

                @Override // com.sonymobile.experienceflow.gles.GLWallpaperService.GLEngine.WallpaperGLSurfaceView
                public GLSurfaceView.Renderer createRenderer(Context context, GLSurfaceView surfaceView) {
                    this.mRenderer = new ExperienceFlowRenderer(context, surfaceView);
                    return this.mRenderer;
                }

                public void stopRendering() {
                    queueEvent(new Runnable() { // from class: com.sonymobile.experienceflow.wallpaper.ExperienceFlowService.1.ExperienceFlowGLSurfaceView.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ExperienceFlowGLSurfaceView.this.mRenderer.getFramerateController().stopRendering();
                        }
                    });
                }

                public void startRendering() {
                    queueEvent(new Runnable() { // from class: com.sonymobile.experienceflow.wallpaper.ExperienceFlowService.1.ExperienceFlowGLSurfaceView.2
                        @Override // java.lang.Runnable
                        public void run() {
                            ExperienceFlowGLSurfaceView.this.mRenderer.getFramerateController().startRendering();
                        }
                    });
                }

                @Override // com.sonymobile.experienceflow.wallpaper.Refreshable
                public void refresh() {
                    queueEvent(new Runnable() { // from class: com.sonymobile.experienceflow.wallpaper.ExperienceFlowService.1.ExperienceFlowGLSurfaceView.3
                        @Override // java.lang.Runnable
                        public void run() {
                            ExperienceFlowGLSurfaceView.this.mRenderer.getFramerateController().forceSingleFrameRender();
                        }
                    });
                }

                public void accelerate(final int value) {
                    queueEvent(new Runnable() { // from class: com.sonymobile.experienceflow.wallpaper.ExperienceFlowService.1.ExperienceFlowGLSurfaceView.4
                        @Override // java.lang.Runnable
                        public void run() {
                            ExperienceFlowGLSurfaceView.this.mRenderer.accelerate(value);
                        }
                    });
                }

                public void handleTouchEvent(MotionEvent event) {
                    SwipeData tmp = this.mFreeList.checkOut();
                    if (tmp == null) {
                        Log.w(Constants.TAG, "FreeList pool is out of objects.");
                        tmp = new SwipeData();
                    }
                    SwipeData dataContainer = tmp;
                    dataContainer.setX(event.getX()).setActionUp(event.getAction() == 1).setFreeList(this.mFreeList).setRenderer(this.mRenderer);
                    if (this.mDelayedEvent != null) {
                        queueEvent(this.mDelayedEvent);
                    }
                    this.mDelayedEvent = dataContainer;
                    if (dataContainer.getActionUp()) {
                        queueEvent(dataContainer);
                        this.mDelayedEvent = null;
                    }
                }
            }
        };
    }
}
