package com.wave.wallpaper

import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.os.SystemClock

interface RenderModeListener {
    fun onStopRendering()
    fun onStartRendering()
}

interface FramerateController {
    fun getMaxFps(): Int
    fun setMaxFps(maxFps: Int)
    fun onFrame(): Long
    fun continueFrame()
    fun setCapFrameRate(cap: Boolean)
    fun getCapFrameRate(): Boolean
    fun startRendering()
    fun stopRendering()
    fun forceSingleFrameRender()
    fun addRenderModeListener(l: RenderModeListener)
    fun removeRenderModeListener(l: RenderModeListener)
}

class ProxyFramerateController(
    maxFps: Int,
    private val surfaceView: GLSurfaceView,
    private val curveRenderer: WaveCurveRenderer
) : FramerateController {

    private var maxFps = maxFps
    private val handler = Handler(Looper.getMainLooper())
    private val listeners = mutableListOf<RenderModeListener>()
    private var rendering = 0
    private var capFrameRate = true
    private var forceSingle = false
    private var startTime = 0L
    private val renderRequest = object : Runnable {
        override fun run() {
            if (rendering > 0 || forceSingle) {
                forceSingle = false
                startTime = SystemClock.elapsedRealtime()
                surfaceView.requestRender()
            }
        }
    }

    override fun getMaxFps() = maxFps
    override fun setMaxFps(maxFps: Int) { this.maxFps = maxFps }
    override fun onFrame(): Long {
        handler.removeCallbacks(renderRequest)
        val end = SystemClock.elapsedRealtime()
        val delta = end - startTime
        val delay = (1000 / maxFps) - delta
        if (delay >= 1) handler.postDelayed(renderRequest, delay) else handler.post(renderRequest)
        return delta
    }
    override fun continueFrame() {
        handler.removeCallbacks(renderRequest)
        val delay = (1000 / maxFps) - (SystemClock.elapsedRealtime() - startTime)
        if (capFrameRate && delay > 1) handler.postDelayed(renderRequest, delay) else renderRequest.run()
    }
    override fun setCapFrameRate(cap: Boolean) { capFrameRate = cap }
    override fun getCapFrameRate() = capFrameRate
    override fun startRendering() {
        synchronized(this) {
            if (rendering < 0) rendering = 0
            val prev = rendering
            rendering++
            if (prev == 0) {
                synchronized(listeners) { listeners.forEach { it.onStartRendering() } }
                handler.removeCallbacks(renderRequest)
                handler.post(renderRequest)
            }
        }
    }
    override fun stopRendering() {
        synchronized(this) {
            rendering--
            if (rendering != 0) {
                if (rendering < 0) rendering = 0
                return
            }
            synchronized(listeners) { listeners.forEach { it.onStopRendering() } }
            handler.removeCallbacks(renderRequest)
        }
    }
    override fun forceSingleFrameRender() {
        forceSingle = true
        handler.removeCallbacks(renderRequest)
        handler.postAtFrontOfQueue(renderRequest)
    }
    override fun addRenderModeListener(l: RenderModeListener) = synchronized(listeners) { listeners.add(l) }
    override fun removeRenderModeListener(l: RenderModeListener) = synchronized(listeners) { listeners.remove(l) }
}
PY