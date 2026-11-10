package com.wave.wallpaper

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Point
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.WindowManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class WaveRenderer(
    private val context: Context,
    private val surfaceView: GLSurfaceView
) : GLSurfaceView.Renderer {

    private var ax = 0f
    private var ay = 0f
    private val curveRenderer = WaveCurveRenderer(context.assets)
    private var doReset = false
    private val location = IntArray(2)
    private var width = 0
    private var height = 0
    val framerateController = ProxyFramerateController(30, surfaceView, curveRenderer)

    init {
        curveRenderer.activate()
        context.getSharedPreferences(PREFS_NAME, 0).registerOnSharedPreferenceChangeListener { sp, key ->
            if (key == PREFS_KEY_COLOR) updateColor(sp)
        }
        framerateController.addRenderModeListener(object : RenderModeListener {
            override fun onStopRendering() { doReset = true }
            override fun onStartRendering() {
                doReset = true
                updateColor(context.getSharedPreferences(PREFS_NAME, 0))
            }
        })
    }

    private fun updateColor(sp: SharedPreferences) {
        curveRenderer.requestColors(context.assets, sp.getInt(PREFS_KEY_COLOR, 0))
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        curveRenderer.setOnRenderListener { framerateController.continueFrame() }
        doReset = true
    }

    override fun onSurfaceChanged(gl: GL10?, w: Int, h: Int) {
        width = w; height = h; doReset = true
    }

    override fun onDrawFrame(gl: GL10?) {
        framerateController.onFrame()
        if (doReset) {
            curveRenderer.deactivate()
            curveRenderer.activate()
            doReset = false
        }
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outSize = Point()
        wm.defaultDisplay?.getRealSize(outSize)
        surfaceView.getLocationOnScreen(location)
        curveRenderer.updateScreenLayout(location[0], location[1], width, height, outSize.x, outSize.y)
        curveRenderer.render(context.assets)
    }

    fun touchDown(x: Float, y: Float) {
        curveRenderer.touchDown(x, y); ax = x; ay = y; framerateController.continueFrame()
    }
    fun touch(x: Float, y: Float) {
        curveRenderer.touch(x, y); ax = x; ay = y; framerateController.continueFrame()
    }
    fun touchUp(x: Float, y: Float) {
        curveRenderer.touchUp(x, y); ax = x; ay = y; framerateController.continueFrame()
    }
    fun poke() = framerateController.continueFrame()
    fun handleSwipe() = framerateController.continueFrame()
    fun accelerate(value: Float) = framerateController.continueFrame()

    companion object {
        const val PREFS_NAME = "WavePrefs"
        const val PREFS_KEY_COLOR = "preferences_color"
    }
}
PY