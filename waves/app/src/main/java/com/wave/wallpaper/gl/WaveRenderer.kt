package com.wave.wallpaper.gl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.wave.wallpaper.model.WaveTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class WaveRenderer(
    private val context: Context,
    private val initialTheme: WaveTheme,
    private val requestRenderCallback: () -> Unit
) : GLSurfaceView.Renderer {

    var theme: WaveTheme = initialTheme
        set(value) {
            field = value
            requestRenderCallback()
        }

    private var programBg = 0
    private var programLine = 0
    private var noiseTex = 0

    private var width = 0
    private var height = 0
    private var time = 0f

    // Parallax scrolling for launcher desktop swipes
    private var targetXOffset = 0.5f
    private var currentXOffset = 0.5f

    private val bgVertices = floatArrayOf(
        -1.0f, -1.0f,
         1.0f, -1.0f,
        -1.0f,  1.0f,
         1.0f,  1.0f
    )
    private val bgVertexBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(bgVertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(bgVertices)
        .apply { position(0) }

    // Pre-allocated buffers for ribbon triangles (2 vertices per step)
    private val numPoints = 80
    private val ribbonVerticesBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(numPoints * 2 * 2 * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    private val ribbonAlphaBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(numPoints * 2 * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    private var touchX = -1f
    private var touchY = -1f
    private var isTouched = false
    private var touchWaveAmplitude = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)

        programBg = ShaderUtil.createProgram(ShaderUtil.BG_VERTEX_SHADER, ShaderUtil.BG_FRAGMENT_SHADER)
        programLine = ShaderUtil.createProgram(ShaderUtil.LINE_VERTEX_SHADER, ShaderUtil.LINE_FRAGMENT_SHADER)
        noiseTex = TextureUtil.loadTextureFromAssets(context, "noise.png")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        time += 0.016f

        // Smooth spring interpolation for parallax scrolling between home screens
        currentXOffset += (targetXOffset - currentXOffset) * 0.10f

        // Touch interaction logic: smoothly decay touch wave amplitude
        if (isTouched) {
            touchWaveAmplitude += (1.0f - touchWaveAmplitude) * 0.1f
        } else {
            touchWaveAmplitude += (0.0f - touchWaveAmplitude) * 0.05f
        }

        drawBackground()
        drawWaves()
    }

    private fun drawBackground() {
        if (programBg == 0) return

        GLES20.glDisable(GLES20.GL_BLEND)
        GLES20.glUseProgram(programBg)

        val posHandle = GLES20.glGetAttribLocation(programBg, "a_position")
        if (posHandle >= 0) {
            GLES20.glEnableVertexAttribArray(posHandle)
            bgVertexBuffer.position(0)
            GLES20.glVertexAttribPointer(posHandle, 2, GLES20.GL_FLOAT, false, 0, bgVertexBuffer)
        }

        val timeHandle = GLES20.glGetUniformLocation(programBg, "u_time")
        if (timeHandle >= 0) GLES20.glUniform1f(timeHandle, time)

        val parallaxHandle = GLES20.glGetUniformLocation(programBg, "u_parallaxOffset")
        if (parallaxHandle >= 0) GLES20.glUniform1f(parallaxHandle, currentXOffset)

        val noiseScaleHandle = GLES20.glGetUniformLocation(programBg, "u_noiseScale")
        if (noiseScaleHandle >= 0) GLES20.glUniform1f(noiseScaleHandle, theme.noiseTextureScale)

        val resHandle = GLES20.glGetUniformLocation(programBg, "u_resolution")
        if (resHandle >= 0) GLES20.glUniform2f(resHandle, max(width.toFloat(), 1f), max(height.toFloat(), 1f))

        // Pass background colors (bgc1 to bgc4)
        for (i in 0 until 4) {
            val color = theme.bgc.getOrNull(i) ?: floatArrayOf(0.1f, 0.2f, 0.4f, 1f)
            val colorHandle = GLES20.glGetUniformLocation(programBg, "u_bgc${i + 1}")
            if (colorHandle >= 0) GLES20.glUniform4fv(colorHandle, 1, color, 0)
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, noiseTex)
        val texHandle = GLES20.glGetUniformLocation(programBg, "u_noiseTex")
        if (texHandle >= 0) GLES20.glUniform1i(texHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        if (posHandle >= 0) GLES20.glDisableVertexAttribArray(posHandle)
    }

    private fun drawWaves() {
        if (programLine == 0) return

        GLES20.glEnable(GLES20.GL_BLEND)
        // Additive blending for glowing silky ribbons
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE)
        GLES20.glUseProgram(programLine)

        val posHandle = GLES20.glGetAttribLocation(programLine, "a_position")
        val alphaHandle = GLES20.glGetAttribLocation(programLine, "a_alpha")
        val colorHandle = GLES20.glGetUniformLocation(programLine, "u_color")

        if (posHandle >= 0) GLES20.glEnableVertexAttribArray(posHandle)
        if (alphaHandle >= 0) GLES20.glEnableVertexAttribArray(alphaHandle)

        val numLines = theme.lineCount.toInt().coerceIn(1, 12)
        // Horizontal shift based on launcher parallax
        val parallaxShift = (currentXOffset - 0.5f) * 1.6f

        for (lineIdx in 0 until numLines) {
            val t = if (numLines > 1) lineIdx.toFloat() / (numLines - 1) else 0f

            val amplitude = interpolate(theme.curveAmplitudeRange, t) * 1.5f + (touchWaveAmplitude * 0.15f)
            val frequency = interpolate(theme.curveFrequencyRange, t) * 2.5f
            val offset = interpolate(theme.curveOffsetRange, t)
            val speed = interpolate(theme.curveSpeedRange, t) * 15.0f
            val bend = interpolate(theme.curveBendRange, t)
            val widthFactor = interpolate(theme.curveWidthRange, t)
            val ribbonHalfWidth = (widthFactor * 0.05f).coerceIn(0.015f, 0.09f)

            val color = theme.lc.getOrNull(lineIdx % theme.lc.size) ?: floatArrayOf(0.5f, 0.7f, 1f, 0.8f)
            if (colorHandle >= 0) GLES20.glUniform4fv(colorHandle, 1, color, 0)

            ribbonVerticesBuffer.position(0)
            ribbonAlphaBuffer.position(0)

            for (i in 0 until numPoints) {
                val x = (i.toFloat() / (numPoints - 1)) * 2.2f - 1.1f // -1.1 to 1.1
                val shiftedX = x - parallaxShift

                // Organic wave calculation combining multiple harmonics
                var y = sin((shiftedX * frequency) + (time * speed) + offset) * amplitude
                y += cos((shiftedX * bend * 1.5f) + (time * 0.4f)) * 0.08f

                // Touch ripple
                if (touchWaveAmplitude > 0.01f) {
                    val dist = abs(x - touchX)
                    val ripple = sin(dist * 12f - time * 6f) * max(0f, 1f - dist * 1.8f) * touchWaveAmplitude * 0.15f
                    y += ripple
                }

                // Top vertex of ribbon
                ribbonVerticesBuffer.put(x)
                ribbonVerticesBuffer.put(y + ribbonHalfWidth)
                ribbonAlphaBuffer.put(0.15f)

                // Bottom vertex of ribbon
                ribbonVerticesBuffer.put(x)
                ribbonVerticesBuffer.put(y - ribbonHalfWidth)
                ribbonAlphaBuffer.put(0.85f)
            }

            ribbonVerticesBuffer.position(0)
            ribbonAlphaBuffer.position(0)

            if (posHandle >= 0) {
                GLES20.glVertexAttribPointer(posHandle, 2, GLES20.GL_FLOAT, false, 0, ribbonVerticesBuffer)
            }
            if (alphaHandle >= 0) {
                GLES20.glVertexAttribPointer(alphaHandle, 1, GLES20.GL_FLOAT, false, 0, ribbonAlphaBuffer)
            }

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, numPoints * 2)
        }

        if (posHandle >= 0) GLES20.glDisableVertexAttribArray(posHandle)
        if (alphaHandle >= 0) GLES20.glDisableVertexAttribArray(alphaHandle)
    }

    private fun interpolate(range: FloatArray, t: Float): Float {
        val min = range.getOrNull(0) ?: 0f
        val max = range.getOrNull(1) ?: min
        return min + (max - min) * t
    }

    fun setParallaxOffset(offset: Float) {
        targetXOffset = offset.coerceIn(0f, 1f)
        requestRenderCallback()
    }

    fun touchDown(x: Float, y: Float) {
        if (width > 0 && height > 0) {
            touchX = (x / width) * 2f - 1f
            touchY = -((y / height) * 2f - 1f)
        }
        isTouched = true
        requestRenderCallback()
    }

    fun touchMove(x: Float, y: Float) {
        if (width > 0 && height > 0) {
            touchX = (x / width) * 2f - 1f
            touchY = -((y / height) * 2f - 1f)
        }
        requestRenderCallback()
    }

    fun touchUp() {
        isTouched = false
        requestRenderCallback()
    }
}
