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
import kotlin.math.cos
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

    private val lineVerticesBuffer: FloatBuffer

    private var touchX = -1f
    private var touchY = -1f
    private var isTouched = false
    private var touchWaveAmplitude = 0f

    init {
        // Pre-allocate buffer for maximum points (e.g., 100 points per line)
        val maxPoints = 100
        lineVerticesBuffer = ByteBuffer
            .allocateDirect(maxPoints * 2 * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(
            theme.clearColor[0],
            theme.clearColor[1],
            theme.clearColor[2],
            theme.clearColor[3]
        )
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        programBg = ShaderUtil.loadShaderFromAssets(context, "gfxbackground.vert", "gfxbackground.frag")
        programLine = ShaderUtil.loadShaderFromAssets(context, "gfxline.vert", "gfxlineA.frag")
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

        // Touch interaction logic: smoothly decay touch wave amplitude
        if (isTouched) {
            touchWaveAmplitude += (1.0f - touchWaveAmplitude) * 0.1f
        } else {
            touchWaveAmplitude += (0.0f - touchWaveAmplitude) * 0.1f
        }

        drawBackground()
        drawWaves()
    }

    private fun drawBackground() {
        if (programBg == 0) return

        GLES20.glUseProgram(programBg)

        val posHandle = GLES20.glGetAttribLocation(programBg, "a_position")
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 2, GLES20.GL_FLOAT, false, 0, bgVertexBuffer)

        val timeHandle = GLES20.glGetUniformLocation(programBg, "u_time")
        GLES20.glUniform1f(timeHandle, time)

        val noiseScaleHandle = GLES20.glGetUniformLocation(programBg, "u_noiseScale")
        GLES20.glUniform1f(noiseScaleHandle, theme.noiseTextureScale)

        // Pass background colors (bgc1 to bgc4)
        for (i in 0 until 4) {
            val color = theme.bgc.getOrNull(i) ?: floatArrayOf(1f, 1f, 1f, 1f)
            val colorHandle = GLES20.glGetUniformLocation(programBg, "u_bgc${i + 1}")
            GLES20.glUniform4fv(colorHandle, 1, color, 0)
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, noiseTex)
        val texHandle = GLES20.glGetUniformLocation(programBg, "u_noiseTex")
        GLES20.glUniform1i(texHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(posHandle)
    }

    private fun drawWaves() {
        if (programLine == 0) return

        GLES20.glUseProgram(programLine)

        val posHandle = GLES20.glGetAttribLocation(programLine, "a_position")
        GLES20.glEnableVertexAttribArray(posHandle)

        val timeHandle = GLES20.glGetUniformLocation(programLine, "u_time")
        GLES20.glUniform1f(timeHandle, time)

        val colorHandle = GLES20.glGetUniformLocation(programLine, "u_color")

        val numLines = theme.lineCount.toInt()
        val numPoints = 100

        for (lineIdx in 0 until numLines) {
            val t = if (numLines > 1) lineIdx.toFloat() / (numLines - 1) else 0f

            // Interpolate parameters based on ranges
            val amplitude = interpolate(theme.curveAmplitudeRange, t) + (touchWaveAmplitude * 0.1f)
            val frequency = interpolate(theme.curveFrequencyRange, t)
            val offset = interpolate(theme.curveOffsetRange, t)
            val widthFactor = interpolate(theme.curveWidthRange, t)

            val color = theme.lc.getOrNull(lineIdx % theme.lc.size) ?: floatArrayOf(1f, 1f, 1f, 1f)
            GLES20.glUniform4fv(colorHandle, 1, color, 0)

            lineVerticesBuffer.position(0)
            for (i in 0 until numPoints) {
                val x = (i.toFloat() / (numPoints - 1)) * 2f - 1f // -1 to 1

                // Base sine wave calculation
                var y = sin((x + time * theme.curveSpeedRange[0] + offset) * frequency) * amplitude

                // Add touch ripple effect
                if (touchWaveAmplitude > 0.01f) {
                    val dist = Math.abs(x - touchX)
                    val ripple = sin(dist * 10f - time * 5f) * Math.max(0f, 1f - dist * 2f) * touchWaveAmplitude
                    y += ripple
                }

                lineVerticesBuffer.put(x)
                lineVerticesBuffer.put(y)
            }
            lineVerticesBuffer.position(0)

            GLES20.glVertexAttribPointer(posHandle, 2, GLES20.GL_FLOAT, false, 0, lineVerticesBuffer)

            // Set line width
            GLES20.glLineWidth(widthFactor * 10f) // Scale it appropriately

            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, numPoints)
        }

        GLES20.glDisableVertexAttribArray(posHandle)
    }

    private fun interpolate(range: FloatArray, t: Float): Float {
        val min = range.getOrNull(0) ?: 0f
        val max = range.getOrNull(1) ?: min
        return min + (max - min) * t
    }

    fun touchDown(x: Float, y: Float) {
        touchX = (x / width) * 2f - 1f
        touchY = -((y / height) * 2f - 1f)
        isTouched = true
        requestRenderCallback()
    }

    fun touchMove(x: Float, y: Float) {
        touchX = (x / width) * 2f - 1f
        touchY = -((y / height) * 2f - 1f)
        requestRenderCallback()
    }

    fun touchUp() {
        isTouched = false
        requestRenderCallback()
    }
}
