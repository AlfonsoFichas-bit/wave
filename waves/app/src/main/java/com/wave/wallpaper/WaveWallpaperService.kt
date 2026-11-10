package com.wave.wallpaper

import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.wave.wallpaper.gl.FramerateController
import com.wave.wallpaper.model.ThemeLoader
import com.wave.wallpaper.service.WaveGLSurfaceView
import com.wave.wallpaper.util.PreferencesManager
import kotlin.system.exitProcess

class WaveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WaveEngine()
    }

    override fun onDestroy() {
        super.onDestroy()
        exitProcess(0)
    }

    inner class WaveEngine : Engine() {
        private var glSurfaceView: WaveGLSurfaceView? = null
        private var framerateController: FramerateController? = null
        private var isVisible = false

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            val prefs = PreferencesManager(this@WaveWallpaperService)
            val themeIndex = prefs.loadThemeIndex()
            val theme = ThemeLoader.loadTheme(this@WaveWallpaperService, themeIndex)

            glSurfaceView = WaveGLSurfaceView(this@WaveWallpaperService, surfaceHolder, theme)

            framerateController = FramerateController {
                glSurfaceView?.onDrawFrame()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            this.isVisible = visible

            if (visible) {
                glSurfaceView?.onResume()
                framerateController?.startRendering()
            } else {
                framerateController?.stopRendering()
                glSurfaceView?.onPause()
            }
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            glSurfaceView?.handleTouchEvent(event)

            if (isVisible) {
                // Force a frame render to react to touch immediately
                framerateController?.forceSingleFrameRender()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            framerateController?.stopRendering()
            // We detach the surface view when destroyed
            glSurfaceView = null
        }
    }
}
