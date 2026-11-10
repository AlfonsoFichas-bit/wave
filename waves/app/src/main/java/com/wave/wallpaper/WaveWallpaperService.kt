package com.wave.wallpaper

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.wave.wallpaper.gl.WaveRenderer
import com.wave.wallpaper.model.ThemeLoader
import com.wave.wallpaper.model.WaveTheme
import com.wave.wallpaper.util.PreferencesManager

class WaveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WaveEngine()
    }

    inner class WaveEngine : Engine() {
        private var glSurfaceView: WaveGLSurfaceView? = null
        private var isVisible = false

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)

            val prefs = PreferencesManager(this@WaveWallpaperService)
            val themeIndex = prefs.loadThemeIndex()
            val theme = ThemeLoader.loadTheme(this@WaveWallpaperService, themeIndex)

            glSurfaceView = WaveGLSurfaceView(this@WaveWallpaperService, theme)
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset)
            glSurfaceView?.setParallaxOffset(xOffset)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            this.isVisible = visible

            if (visible) {
                // Refresh theme in case the user changed it in the app
                val prefs = PreferencesManager(this@WaveWallpaperService)
                val themeIndex = prefs.loadThemeIndex()
                val theme = ThemeLoader.loadTheme(this@WaveWallpaperService, themeIndex)
                glSurfaceView?.renderer?.theme = theme

                glSurfaceView?.onResume()
            } else {
                glSurfaceView?.onPause()
            }
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            glSurfaceView?.handleTouchEvent(event)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            glSurfaceView?.onPause()
        }

        override fun onDestroy() {
            super.onDestroy()
            glSurfaceView?.onDestroy()
            glSurfaceView = null
        }

        inner class WaveGLSurfaceView(
            context: Context,
            theme: WaveTheme
        ) : GLSurfaceView(context) {

            val renderer: WaveRenderer

            init {
                setEGLContextClientVersion(2)
                setEGLConfigChooser(8, 8, 8, 0, 0, 0)
                this@WaveEngine.surfaceHolder.setFormat(PixelFormat.TRANSLUCENT)
                preserveEGLContextOnPause = true

                renderer = WaveRenderer(context, theme) {
                    requestRender()
                }

                setRenderer(renderer)
                renderMode = RENDERMODE_CONTINUOUSLY
            }

            override fun getHolder(): SurfaceHolder {
                return this@WaveEngine.surfaceHolder
            }

            fun onDestroy() {
                super.onDetachedFromWindow()
            }

            fun setParallaxOffset(xOffset: Float) {
                queueEvent {
                    renderer.setParallaxOffset(xOffset)
                }
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
        }
    }
}
