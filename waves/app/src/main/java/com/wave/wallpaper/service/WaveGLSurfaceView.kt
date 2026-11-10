package com.wave.wallpaper.service

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.wave.wallpaper.gl.WaveRenderer
import com.wave.wallpaper.model.WaveTheme

class WaveGLSurfaceView(
    context: Context,
    private val engineHolder: SurfaceHolder,
    theme: WaveTheme
) : GLSurfaceView(context) {

    private val renderer: WaveRenderer

    init {
        setEGLContextClientVersion(2)
        setEGLConfigChooser(8, 8, 8, 0, 0, 0)
        engineHolder.setFormat(PixelFormat.TRANSLUCENT)
        preserveEGLContextOnPause = true

        renderer = WaveRenderer(context, theme) {
            requestRender()
        }

        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun getHolder(): SurfaceHolder {
        return engineHolder
    }

    fun handleTouchEvent(event: MotionEvent) {
        queueEvent {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> renderer.touchDown(event.x, event.y)
                MotionEvent.ACTION_MOVE -> renderer.touchMove(event.x, event.y)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> renderer.touchUp()
            }
        }
    }

    fun onDrawFrame() {
        requestRender()
    }
}
