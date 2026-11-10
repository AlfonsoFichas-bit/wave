package com.wave.wallpaper

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder

/**
 * Live wallpaper "wave" (clon de Sony Experience Flow).
 * Renderiza ondas/curvas con OpenGL ES 2.0 sobre un GLSurfaceView.
 */
class WaveWallpaperService : WallpaperService() {

    override fun onCreate() { super.onCreate() }

    override fun onDestroy() {
        super.onDestroy()
        System.exit(0)
    }

    override fun onCreateEngine(): Engine = WaveEngine(this)

    inner class WaveEngine(private val service: WaveWallpaperService) : Engine() {
        private var mView: WaveGLSurfaceView? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)
            mView = WaveGLSurfaceView(service)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) mView?.startRendering() else mView?.stopRendering()
        }

        override fun onDestroy() {
            super.onDestroy()
            mView?.onDestroy()
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            val r = mView?.renderer ?: return
            mView?.handleTouchEvent(event)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> r.touchDown(event.x, event.y)
                MotionEvent.ACTION_UP -> r.touchUp(event.x, event.y)
                MotionEvent.ACTION_MOVE -> r.touch(event.x, event.y)
            }
        }

        override fun onCommand(action: String?, x: Int, y: Int, z: Int, extras: Bundle?, resultRequested: Boolean): Bundle? {
            // Comandos opcionales (aceleración)
            if (action == "com.wave.wallpaper.command.ACCELERATE" && kotlin.math.abs(x) <= 10000) {
                mView?.accelerate(x)
            }
            return super.onCommand(action, x, y, z, extras, resultRequested)
        }

        fun createGLSurfaceView(): WaveGLSurfaceView = WaveGLSurfaceView(service).also { mView = it }

        inner class WaveGLSurfaceView(context: Context) : GLSurfaceView(context) {
            val renderer: WaveRenderer
            private val freeList = FreeList<SwipeData>(SwipeData::class.java, 50)
            private var delayedEvent: SwipeData? = null

            init {
                setEGLContextClientVersion(2)
                setEGLConfigChooser(8, 8, 8, 0, 0, 0)
                holder.setFormat(android.graphics.PixelFormat.TRANSLUCENT)
                setPreserveEGLContextOnPause(true)
                renderer = WaveRenderer(context, this)
                setRenderer(renderer)
                setRenderMode(RENDERMODE_WHEN_DIRTY)
            }

            fun startRendering() = queueEvent { renderer.framerateController.startRendering() }
            fun stopRendering() = queueEvent { renderer.framerateController.stopRendering() }
            fun refresh() = queueEvent { renderer.framerateController.forceSingleFrameRender() }
            fun accelerate(value: Int) = queueEvent { renderer.accelerate(value.toFloat()) }

            fun handleTouchEvent(event: MotionEvent) {
                val tmp = freeList.checkOut() ?: SwipeData()
                val data = tmp.setX(event.x)
                    .setActionUp(event.action == MotionEvent.ACTION_UP)
                    .setFreeList(freeList)
                    .setRenderer(renderer)
                if (delayedEvent != null) queueEvent(delayedEvent)
                delayedEvent = data
                if (data.actionUp) {
                    queueEvent(data)
                    delayedEvent = null
                }
            }
        }
    }
}
PY