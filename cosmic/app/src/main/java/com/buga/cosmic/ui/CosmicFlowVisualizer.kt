package com.buga.cosmic.ui

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.buga.cosmic.gl.CosmicFlowRenderer
import com.buga.cosmic.theme.CosmicTheme

@Composable
fun CosmicFlowVisualizer(
    modifier: Modifier = Modifier,
    theme: CosmicTheme = CosmicTheme.AMETHYST,
    bpm: Float = 120f,
    amplitude: Float = 1.0f,
    onSurfaceReady: ((CosmicFlowRenderer) -> Unit)? = null
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var renderer by remember { mutableStateOf<CosmicFlowRenderer?>(null) }
    var glSurfaceView by remember { mutableStateOf<GLSurfaceView?>(null) }
    var parallaxOffset by remember { mutableFloatStateOf(0.5f) }

    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Map horizontal drag to parallax [0.0, 1.0] range
                        // Screen width could be used, but relative offset is simpler
                        parallaxOffset -= (dragAmount.x / size.width.toFloat()) * 2f
                        parallaxOffset = parallaxOffset.coerceIn(0.0f, 1.0f)
                        renderer?.setParallaxOffset(parallaxOffset)
                    },
                    onDragEnd = {
                        // Spring back to center smoothly (would need an animation coroutine)
                        // For now we just reset instantly or keep it where it is
                        parallaxOffset = 0.5f
                        renderer?.setParallaxOffset(parallaxOffset)
                    }
                )
            },
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)

                // Set default surface config to properly render and blend colors
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)

                val flowRenderer = CosmicFlowRenderer(context)
                setRenderer(flowRenderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

                renderer = flowRenderer
                glSurfaceView = this
                onSurfaceReady?.invoke(flowRenderer)
            }
        },
        update = {
            // Unused since we handle property updates via remember state below
        }
    )

    LaunchedEffect(theme, bpm, amplitude, renderer) {
        renderer?.let {
            it.setTheme(theme)
            it.setBpm(bpm)
            it.setAmplitude(amplitude)
        }
    }

    DisposableEffect(lifecycleOwner, glSurfaceView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> glSurfaceView?.onResume()
                Lifecycle.Event.ON_PAUSE -> glSurfaceView?.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
