# Código decompilado real (jadx) de `wallpaper_wave.apk` — referencia para clon "wave"

> Decompilado con jadx 1.5.0 sobre `classes.dex`. Código original bajo copyright de Sony Mobile; se usa aquí únicamente para análisis e ingeniería inversa.

---

## 1. `GLWallpaperService.java` (base del wallpaper GL)

```java
package com.sonymobile.experienceflow.gles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public abstract class GLWallpaperService extends WallpaperService {
    public abstract class GLEngine extends WallpaperService.Engine {
        protected WallpaperGLSurfaceView mGLSurfaceView;
        private GLSurfaceView.Renderer mRenderer;

        public abstract WallpaperGLSurfaceView createGLSurfaceView();

        public GLEngine() {
            super(GLWallpaperService.this);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.mGLSurfaceView = createGLSurfaceView();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (this.mGLSurfaceView != null && this.mRenderer != null) {
                if (visible) this.mGLSurfaceView.onResume();
                else this.mGLSurfaceView.onPause();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (this.mGLSurfaceView != null) this.mGLSurfaceView.onDestroy();
        }

        protected void setRenderer(GLSurfaceView.Renderer renderer) {
            this.mRenderer = renderer;
            if (this.mGLSurfaceView != null) {
                this.mGLSurfaceView.setRenderer(renderer);
                this.mGLSurfaceView.onResume();
            }
        }

        public abstract class WallpaperGLSurfaceView extends GLSurfaceView {
            protected GLSurfaceView.Renderer mRenderer;

            public abstract GLSurfaceView.Renderer createRenderer(Context context, GLSurfaceView gLSurfaceView);

            public WallpaperGLSurfaceView(Context context) {
                super(context);
                setEGLContextClientVersion(2);
                setEGLConfigChooser(8, 8, 8, 0, 0, 0);
                getHolder().setFormat(3); // TRANSLUCENT
                setPreserveEGLContextOnPause(true);
                this.mRenderer = createRenderer(GLWallpaperService.this.getBaseContext(), this);
                setRenderer(this.mRenderer);
                setRenderMode(0); // RENDERMODE_WHEN_DIRTY
            }

            @Override
            public SurfaceHolder getHolder() {
                return GLEngine.this.getSurfaceHolder();
            }

            public void onDestroy() {
                super.onDetachedFromWindow();
            }
        }
    }
}
```

---

## 2. `ExperienceFlowService.java` (wallpaper concreto)

```java
package com.sonymobile.experienceflow.wallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.sonymobile.experienceflow.gles.GLWallpaperService;
import com.sonymobile.experienceflow.renderer.ExperienceFlowRenderer;
import com.sonymobile.experienceflow.util.Constants;
import com.sonymobile.experienceflow.util.FreeList;

public class ExperienceFlowService extends GLWallpaperService {
    @Override
    public void onCreate() { super.onCreate(); }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public WallpaperService.Engine onCreateEngine() {
        return new GLWallpaperService.GLEngine() {
            private ExperienceFlowGLSurfaceView mView;

            @Override
            public void onCreate(SurfaceHolder surfaceHolder) {
                super.onCreate(surfaceHolder);
                this.mView = (ExperienceFlowGLSurfaceView) this.mGLSurfaceView;
            }

            @Override
            public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
                if (LockscreenApiConstants.LOCKSCREEN_WALLPAPER_COMMAND_POINTER_DOWN.equals(action)) {
                    this.mView.mRenderer.touchDown(x, y);
                } else if (LockscreenApiConstants.LOCKSCREEN_WALLPAPER_COMMAND_POINTER_MOVE.equals(action)) {
                    this.mView.mRenderer.touch(x, y);
                } else if (LockscreenApiConstants.LOCKSCREEN_WALLPAPER_COMMAND_POINTER_UP.equals(action)) {
                    this.mView.mRenderer.touchUp(x, y);
                } else if (LockscreenApiConstants.LOCKSCREEN_WALLPAPER_COMMAND_ACCELERATE.equals(action)
                        && Math.abs(x) <= LockscreenApiConstants.LOCKSCREEN_WALLPAPER_MAX_ACCELERATE_VALUE) {
                    this.mView.accelerate(x);
                }
                return super.onCommand(action, x, y, z, extras, resultRequested);
            }

            @Override
            public void onTouchEvent(MotionEvent event) {
                super.onTouchEvent(event);
                this.mView.handleTouchEvent(event);
                switch (event.getAction()) {
                    case 0: this.mView.mRenderer.touchDown(event.getX(), event.getY()); return;
                    case 1: this.mView.mRenderer.touchUp(event.getX(), event.getY()); return;
                    case 2: this.mView.mRenderer.touch(event.getX(), event.getY()); return;
                }
            }

            @Override
            public GLWallpaperService.GLEngine.WallpaperGLSurfaceView createGLSurfaceView() {
                return new ExperienceFlowGLSurfaceView(ExperienceFlowService.this);
            }

            @Override
            public void onVisibilityChanged(boolean visible) {
                super.onVisibilityChanged(visible);
                if (visible) this.mView.startRendering();
                else this.mView.stopRendering();
            }

            class ExperienceFlowGLSurfaceView extends GLWallpaperService.GLEngine.WallpaperGLSurfaceView implements Refreshable {
                private SwipeData mDelayedEvent;
                private FreeList<SwipeData> mFreeList;
                private ExperienceFlowRenderer mRenderer;

                public ExperienceFlowGLSurfaceView(Context context) {
                    super(context);
                    this.mFreeList = new FreeList<>(SwipeData.class, 50);
                }

                @Override
                public GLSurfaceView.Renderer createRenderer(Context context, GLSurfaceView surfaceView) {
                    this.mRenderer = new ExperienceFlowRenderer(context, surfaceView);
                    return this.mRenderer;
                }

                public void stopRendering() {
                    queueEvent(() -> this.mRenderer.getFramerateController().stopRendering());
                }
                public void startRendering() {
                    queueEvent(() -> this.mRenderer.getFramerateController().startRendering());
                }
                public void refresh() {
                    queueEvent(() -> this.mRenderer.getFramerateController().forceSingleFrameRender());
                }
                public void accelerate(int value) {
                    queueEvent(() -> this.mRenderer.accelerate(value));
                }

                public void handleTouchEvent(MotionEvent event) {
                    SwipeData tmp = this.mFreeList.checkOut();
                    if (tmp == null) { tmp = new SwipeData(); }
                    SwipeData dataContainer = tmp;
                    dataContainer.setX(event.getX())
                        .setActionUp(event.getAction() == 1)
                        .setFreeList(this.mFreeList)
                        .setRenderer(this.mRenderer);
                    if (this.mDelayedEvent != null) queueEvent(this.mDelayedEvent);
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
```

---

## 3. `ExperienceFlowRenderer.java` (renderer GL)

```java
package com.sonymobile.experienceflow.renderer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.WindowManager;
import com.sonymobile.experienceflow.wallpaper.ColorPicker;
import com.sonymobile.experienceflow.wallpaper.ExperienceFlowPreferencesActivity;
import com.sonymobile.experienceflow.wallpaper.SwipeData;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ExperienceFlowRenderer implements GLSurfaceView.Renderer {
    float ax, ay;
    protected CurveRendererImpl curveRenderer;
    boolean doReset = false;
    int[] location = new int[2];
    private final Context mContext;
    private FramerateControllerBase mFramerateController;
    private int mHeight;
    private GLSurfaceView mSurfaceView;
    private int mWidth;

    private void updateColor(SharedPreferences sharedPreferences) {
        this.curveRenderer.requestColors(this.mContext.getAssets(),
            sharedPreferences.getInt(ExperienceFlowPreferencesActivity.PREFS_KEY_COLOR,
                ColorPicker.getDefaultLiveWallpaperIndex(this.mContext)));
    }

    public ExperienceFlowRenderer(Context context, GLSurfaceView surfaceView) {
        this.mContext = context;
        this.mSurfaceView = surfaceView;
        this.curveRenderer = new CurveRendererImpl(this.mContext.getAssets());
        this.curveRenderer.activate();
        context.getSharedPreferences(ExperienceFlowPreferencesActivity.SHARED_PREFS_NAME, 0)
            .registerOnSharedPreferenceChangeListener((sp, key) -> updateColor(sp));
        getFramerateController().addRenderModeListener(new RenderModeListener() {
            public void onStopRendering() { doReset = true; }
            public void onStartRendering() {
                doReset = true;
                updateColor(mContext.getSharedPreferences(ExperienceFlowPreferencesActivity.SHARED_PREFS_NAME, 0));
            }
        });
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        this.curveRenderer.setOnRenderListener(() -> getFramerateController().continueFrame());
        this.doReset = true;
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        this.doReset = true;
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        getFramerateController().onFrame();
        if (this.doReset) {
            this.curveRenderer.deactivate();
            this.curveRenderer.activate();
            this.doReset = false;
        }
        Point outSize = new Point();
        WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
        if (wm.getDefaultDisplay() != null) {
            wm.getDefaultDisplay().getRealSize(outSize);
            this.mSurfaceView.getLocationOnScreen(this.location);
            this.curveRenderer.updateScreenLayout(this.location[0], this.location[1],
                this.mWidth, this.mHeight, outSize.x, outSize.y);
        }
        this.curveRenderer.render(this.mContext.getAssets());
    }

    public void pushTouchUp() { touchUp(this.ax, this.ay); }

    public Capabilities getCapabilities() {
        Capabilities c = new Capabilities();
        String ext = GLES20.glGetString(7939);
        java.util.StringTokenizer st = new java.util.StringTokenizer(ext);
        int n = st.countTokens();
        String[] arr = new String[n];
        for (int i = 0; i < n; i++) arr[i] = st.nextToken();
        c.setExtensions(arr);
        return c;
    }

    public void touchDown(float x, float y) {
        this.curveRenderer.touchDown(x, y);
        this.ax = x; this.ay = y;
        getFramerateController().continueFrame();
    }
    public void touch(float x, float y) {
        this.curveRenderer.touch(x, y);
        this.ax = x; this.ay = y;
        getFramerateController().continueFrame();
    }
    public void touchUp(float x, float y) {
        this.curveRenderer.touchUp(x, y);
        this.ax = x; this.ay = y;
        getFramerateController().continueFrame();
    }
    public void poke() { getFramerateController().continueFrame(); }
    public void handleSwipe(SwipeData dc) { getFramerateController().continueFrame(); }
    public void accelerate(float value) { getFramerateController().continueFrame(); }

    public FramerateControllerBase getFramerateController() {
        if (this.mFramerateController == null) {
            this.mFramerateController = new ProxyFramerateController(30, this.mSurfaceView, this.curveRenderer);
        }
        return this.mFramerateController;
    }
}
```

---

## 4. `CurveRenderer.java` + `CurveRendererImpl.java` (JNI bridge)

```java
// CurveRenderer.java
package com.sonymobile.experienceflow.renderer;
import android.content.res.AssetManager;
public abstract class CurveRenderer {
    protected native synchronized void activate(int i);
    protected native synchronized void deactivate(int i);
    protected native synchronized int getSleepRecommendation(int i);
    protected native synchronized int init(AssetManager assetManager);
    protected native synchronized void render(int i, AssetManager assetManager);
    protected native synchronized void requestColors(int i, AssetManager assetManager, int i2);
    protected native synchronized void touch(int i, float f, float f2);
    protected native synchronized void touchDown(int i, float f, float f2);
    protected native synchronized void touchUp(int i, float f, float f2);
    protected native synchronized void updateScreenLayout(int i, int i2, int i3, int i4, int i5, int i6, int i7);
    static { System.loadLibrary("lwplocal"); }
}

// CurveRendererImpl.java
package com.sonymobile.experienceflow.renderer;
import android.content.res.AssetManager;
public class CurveRendererImpl extends CurveRenderer {
    int id;
    protected RenderListener renderListener;
    public interface RenderListener { void pleaseRender(); }

    protected synchronized void pleaseRender() {
        if (this.renderListener != null) {
            synchronized (this) { this.renderListener.pleaseRender(); }
        }
    }
    public CurveRendererImpl(AssetManager am) { this.id = init(am); }
    public synchronized void updateScreenLayout(int x, int y, int w, int h, int sw, int sh) {
        super.updateScreenLayout(this.id, x, y, w, h, sw, sh);
    }
    public synchronized void requestColors(AssetManager am, int theme) {
        super.requestColors(this.id, am, theme); pleaseRender();
    }
    public synchronized void touchDown(float x, float y) { super.touchDown(this.id, x, y); pleaseRender(); }
    public synchronized void touch(float x, float y) { super.touch(this.id, x, y); pleaseRender(); }
    public synchronized void touchUp(float x, float y) { super.touchUp(this.id, x, y); pleaseRender(); }
    public synchronized void activate() { super.activate(this.id); pleaseRender(); }
    public synchronized void deactivate() { super.deactivate(this.id); pleaseRender(); }
    public synchronized void setOnRenderListener(RenderListener l) { this.renderListener = l; }
    public synchronized void render(AssetManager am) { super.render(this.id, am); }
    public synchronized int getSleepRecommendation() { return super.getSleepRecommendation(this.id); }
}
```

---

## 5. Constantes y claves

```java
// Constants.java
public class Constants {
    public static final String ANDROID_PACKAGE = "com.sonymobile.experienceflow2";
    public static final String TAG = "ExperienceFlow2";
    public static final String WALLPAPER_SERVICE_NAME = "com.sonymobile.experienceflow.wallpaper.ExperienceFlowService";
}

// LockscreenApiConstants.java
public class LockscreenApiConstants {
    public static final String LOCKSCREEN_WALLPAPER_COMMAND_ACCELERATE = "com.sonymobile.lockscreen.wallpaper.command.ACCELERATE";
    public static final String LOCKSCREEN_WALLPAPER_COMMAND_POINTER_DOWN = "com.sonymobile.lockscreen.wallpaper.command.POINTER_DOWN";
    public static final String LOCKSCREEN_WALLPAPER_COMMAND_POINTER_MOVE = "com.sonymobile.lockscreen.wallpaper.command.POINTER_MOVE";
    public static final String LOCKSCREEN_WALLPAPER_COMMAND_POINTER_UP = "com.sonymobile.lockscreen.wallpaper.command.POINTER_UP";
    public static final int LOCKSCREEN_WALLPAPER_MAX_ACCELERATE_VALUE = 10000;
    public static final String PERMISSION_INJECT_DEPENDENCIES = "com.sonymobile.experienceflow2.permission.INJECT_DEPENDENCIES";
}

// ExperienceFlowPreferencesActivity
public static final String PREFS_KEY_COLOR = "preferences_color";
public static final String SHARED_PREFS_NAME = "ExperienceFlowPrefs";
public static final String LOG_TAG = "ExperienceFlowPreferencesActivity";
```

---

## 6. `ColorPicker` + `ColorInfo` (paleta)

```java
// ColorInfo: 13 colores con index, nameID, thumbnailID, label
// XperiaVerizonRed(-2), Xperia(-1), Blue(0), Purple(1), Red(2), Orange(3),
// Green(4), Gray(5), Black(6), Copper(7), SeaGreen(12), AquaBlue(13),
// WarmSilver(14), Rose(15)

// ColorPicker.getColorIndexFromPreferences:
//   if prefs contains "preferences_color": return prefs.getInt(..., defaultIndex)
//   else: return ThemeChangeReceiver.getThemeWallpaperColor(context)
// ColorPicker.getSemcColors(context): selecciona grupo según R.integer.config_colorGroupID (0..5)
```

---

## 7. FramerateController (control de FPS)

- `ProxyFramerateController(30, surfaceView, curveRenderer)` a 30 FPS máx.
- `onFrame()`: calcula delay, programa RenderRequest con postDelayed.
- `continueFrame()`: fuerza render si no hay cap.
- `startRendering()/stopRendering()`: notifica listeners (onStartRendering/onStopRendering) que ponen `doReset=true`.
- `forceSingleFrameRender()`: renderiza un frame (usado por refresh).

---

## 8. Pseudocódigo exacto del clon "wave" (basado en código real)

### 8.1 Service
```
class WaveWallpaperService : GLWallpaperService {
    override fun onCreate() { super.onCreate() }
    override fun onDestroy() { super.onDestroy(); System.exit(0) }
    override fun onCreateEngine() = object : GLEngine() {
        var mView: WaveGLSurfaceView
        override fun onCreate(holder) { super.onCreate(holder); mView = mGLSurfaceView as WaveGLSurfaceView }
        override fun onCommand(action, x, y, z, extras, resultRequested): Bundle {
            when (action) {
                "POINTER_DOWN" -> mView.mRenderer.touchDown(x, y)
                "POINTER_MOVE" -> mView.mRenderer.touch(x, y)
                "POINTER_UP"   -> mView.mRenderer.touchUp(x, y)
                "ACCELERATE"   -> if (abs(x) <= 10000) mView.accelerate(x)
            }
            return super.onCommand(...)
        }
        override fun onTouchEvent(event) {
            super.onTouchEvent(event)
            mView.handleTouchEvent(event)
            when (event.action) {
                ACTION_DOWN -> mView.mRenderer.touchDown(event.x, event.y)
                ACTION_UP   -> mView.mRenderer.touchUp(event.x, event.y)
                ACTION_MOVE -> mView.mRenderer.touch(event.x, event.y)
            }
        }
        override fun createGLSurfaceView() = WaveGLSurfaceView(this)
        override fun onVisibilityChanged(visible) {
            super.onVisibilityChanged(visible)
            if (visible) mView.startRendering() else mView.stopRendering()
        }
        inner class WaveGLSurfaceView(context) : WallpaperGLSurfaceView(context), Refreshable {
            var mRenderer: WaveRenderer
            val mFreeList = FreeList<SwipeData>(SwipeData::class, 50)
            override fun createRenderer(ctx, view) = WaveRenderer(ctx, view).also { mRenderer = it }
            fun stopRendering() = queueEvent { mRenderer.framerateController.stopRendering() }
            fun startRendering() = queueEvent { mRenderer.framerateController.startRendering() }
            fun refresh() = queueEvent { mRenderer.framerateController.forceSingleFrameRender() }
            fun accelerate(v) = queueEvent { mRenderer.accelerate(v) }
            fun handleTouchEvent(event) { /* FreeList de SwipeData, deferred queue */ }
        }
    }
}
```

### 8.2 Renderer
```
class WaveRenderer(ctx, surfaceView) : GLSurfaceView.Renderer {
    var ax, ay
    var curveRenderer = WaveCurveRenderer(ctx.assets) // JNI o port Kotlin
    var doReset = false
    var location = intArrayOf(2)
    var width, height
    var framerateController = ProxyFramerateController(30, surfaceView, curveRenderer)

    init {
        curveRenderer.activate()
        ctx.getSharedPreferences("WavePrefs", 0).registerOnSharedPreferenceChangeListener { sp, key ->
            if (key == "preferences_color") updateColor(sp)
        }
        framerateController.addRenderModeListener(object : RenderModeListener {
            override fun onStopRendering() { doReset = true }
            override fun onStartRendering() {
                doReset = true
                updateColor(ctx.getSharedPreferences("WavePrefs", 0))
            }
        })
    }

    override fun onSurfaceCreated(gl, config) {
        curveRenderer.setOnRenderListener { framerateController.continueFrame() }
        doReset = true
    }
    override fun onSurfaceChanged(gl, w, h) { width = w; height = h; doReset = true }

    override fun onDrawFrame(gl) {
        framerateController.onFrame()
        if (doReset) { curveRenderer.deactivate(); curveRenderer.activate(); doReset = false }
        val outSize = Point()
        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay?.getRealSize(outSize)
        surfaceView.getLocationOnScreen(location)
        curveRenderer.updateScreenLayout(location[0], location[1], width, height, outSize.x, outSize.y)
        curveRenderer.render(ctx.assets)
    }

    fun touchDown(x, y) { curveRenderer.touchDown(x, y); ax=x; ay=y; framerateController.continueFrame() }
    fun touch(x, y) { curveRenderer.touch(x, y); ax=x; ay=y; framerateController.continueFrame() }
    fun touchUp(x, y) { curveRenderer.touchUp(x, y); ax=x; ay=y; framerateController.continueFrame() }
    fun poke() = framerateController.continueFrame()
    fun handleSwipe(dc) = framerateController.continueFrame()
    fun accelerate(v) = framerateController.continueFrame()

    private fun updateColor(sp) {
        curveRenderer.requestColors(ctx.assets,
            sp.getInt("preferences_color", defaultColorIndex))
    }
}
```

### 8.3 JNI nativo (si se porta liblwplocal.so)
```
// CurveRenderer (abstract) native methods:
native fun activate(id: Int)
native fun deactivate(id: Int)
native fun getSleepRecommendation(id: Int): Int
native fun init(am: AssetManager): Int
native fun render(id: Int, am: AssetManager)
native fun requestColors(id: Int, am: AssetManager, theme: Int)
native fun touch(id: Int, x: Float, y: Float)
native fun touchDown(id: Int, x: Float, y: Float)
native fun touchUp(id: Int, x: Float, y: Float)
native fun updateScreenLayout(id, x, y, w, h, sw, sh: Int)
static { System.loadLibrary("lwplocal") }
```

### 8.4 Port a Kotlin puro (sin native) — esqueleto del render
```kotlin
class WaveRenderer(ctx, view) : GLSurfaceView.Renderer {
    var width=0; height=0
    var programBg=0; programLine=0; var noiseTex=0
    var theme = parseTheme(readAsset("lwp.0.theme"))
    var time=0f

    override fun onSurfaceCreated(gl, config) {
        GLES20.glClearColor(0f,0f,0f,0f)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        programBg = buildProgram("shaders/gfxbackground.vert", "shaders/gfxbackground.frag")
        programLine = buildProgram("shaders/gfxline.vert", "shaders/gfxlineA.frag")
        noiseTex = loadTexture("noise.png")
    }
    override fun onSurfaceChanged(gl, w, h) { width=w; height=h; GLES20.glViewport(0,0,w,h) }
    override fun onDrawFrame(gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        time += 0.016f
        // fondo
        GLES20.glUseProgram(programBg)
        setUniform(programBg, "u_time", time)
        setUniform(programBg, "u_noiseScale", theme.noiseTextureScale)
        setUniform(programBg, "u_clearColor", theme.clearColor)
        for (i in 0..3) setUniform(programBg, "u_bgc${i+1}", theme.bgc[i])
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, noiseTex)
        drawFullscreenQuad()
        // curvas
        GLES20.glUseProgram(programLine)
        val n = theme.lineCount.toInt().coerceAtLeast(1)
        for (i in 0 until n) {
            val amp = lerp(theme.curveAmplitudeRange[0], theme.curveAmplitudeRange[1], hash(i))
            // ... calcular params
            setUniform(programLine, "u_color", theme.lc[i % theme.lc.size])
            drawLineStrip(computeCurve(i, time))
        }
    }
    fun touchDown(x,y) { /* activar efecto de onda */ }
    fun touch(x,y) { /* actualizar */ }
    fun touchUp(x,y) { /* desactivar */ }
}
```

---

## 9. Claves de SharedPreferences

```
SHARED_PREFS_NAME = "ExperienceFlowPrefs"
PREFS_KEY_COLOR   = "preferences_color"   (int, index del ColorInfo)
```

---

## 10. Assets necesarios para el clon

- `assets/shaders/`: gfxbackground.vert/frag + header, gfxline.vert, gfxlineA.frag, gfxlineB.frag, header/frag/vert.base, footer/frag/vert.base, inputs.base
- `assets/themes/`: lwp.-2.theme (default), lwp.-1.theme (transitions), lwp.0..15.theme
- `assets/noise.png` (textura de ruido)
- `res/drawable-*/`: previews por tema + ic_launcher/ic_wallpaper
- `res/raw/defaultcontainer_binary` (analytics — omitir o reemplazar)
- `res/xml/wallpaper.xml`

---

## 11. Archivos de referencia copiados

- Código decompilado: `C:\Users\alfon\TGZ\wave\reference_java\`
- Assets extraídos: `C:\Users\alfon\TGZ\wave\clone_assets\`
- Shaders/temas originales: `clone_assets/assets/`

*Documento generado a partir de código decompilado con jadx 1.5.0. Solo para análisis.*