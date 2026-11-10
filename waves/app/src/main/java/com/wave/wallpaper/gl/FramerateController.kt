package com.wave.wallpaper.gl

import android.os.Handler
import android.os.Looper
import android.os.SystemClock

class FramerateController(private val renderCallback: () -> Unit) {
    private val handler = Handler(Looper.getMainLooper())
    private var isRendering = false
    private var lastFrameTime = 0L
    private val targetFrameTimeMs = 1000L / 30L // Cap at 30 FPS

    private val renderRunnable = object : Runnable {
        override fun run() {
            if (!isRendering) return

            val currentTime = SystemClock.elapsedRealtime()
            val timeSinceLastFrame = currentTime - lastFrameTime

            if (timeSinceLastFrame >= targetFrameTimeMs) {
                renderCallback()
                lastFrameTime = currentTime
            }

            val delay = Math.max(0, targetFrameTimeMs - (SystemClock.elapsedRealtime() - lastFrameTime))
            handler.postDelayed(this, delay)
        }
    }

    fun startRendering() {
        if (!isRendering) {
            isRendering = true
            lastFrameTime = SystemClock.elapsedRealtime()
            handler.post(renderRunnable)
        }
    }

    fun stopRendering() {
        isRendering = false
        handler.removeCallbacks(renderRunnable)
    }

    fun forceSingleFrameRender() {
        if (!isRendering) {
            renderCallback()
            lastFrameTime = SystemClock.elapsedRealtime()
        }
    }
}
