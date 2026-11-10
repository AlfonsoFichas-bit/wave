package com.wave.wallpaper.ui

import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.wave.wallpaper.gl.WaveRenderer
import com.wave.wallpaper.model.WaveTheme

@Composable
fun WallpaperPreview(theme: WaveTheme, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val glSurfaceView = remember {
        GLSurfaceView(context).apply {
            setEGLContextClientVersion(2)
            setEGLConfigChooser(8, 8, 8, 8, 16, 0)
            holder.setFormat(PixelFormat.TRANSLUCENT)
        }
    }

    val renderer = remember {
        WaveRenderer(context, theme) {
            glSurfaceView.requestRender()
        }
    }

    DisposableEffect(theme) {
        renderer.theme = theme
        onDispose { }
    }

    AndroidView(
        factory = {
            glSurfaceView.apply {
                setRenderer(renderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

                setOnTouchListener { _, event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> renderer.touchDown(event.x, event.y)
                        MotionEvent.ACTION_MOVE -> renderer.touchMove(event.x, event.y)
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> renderer.touchUp()
                    }
                    true
                }
            }
        },
        modifier = modifier.fillMaxSize()
    )

    DisposableEffect(glSurfaceView) {
        glSurfaceView.onResume()
        onDispose {
            glSurfaceView.onPause()
        }
    }
}
