package com.buga.cosmic.gl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.buga.cosmic.theme.CosmicTheme
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CosmicFlowRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val mesh = CosmicMesh(48, 48)
    private val matrixState = CosmicMatrixState()

    @Volatile
    private var currentTheme: CosmicTheme = CosmicTheme.DEFAULT

    @Volatile
    private var currentBpm: Float = 120f

    @Volatile
    private var currentAmplitude: Float = 1.0f

    private var backgroundProgramId = 0
    private var flowProgramId = 0

    private var bgPositionHandle = 0
    private var bgTexCoordHandle = 0
    private var bgTimeHeightScaleColorXOffsetHandle = 0
    private var bgPrimaryColorHandle = 0
    private var bgTextureHandle = 0

    private var flowMVPMatrixHandle = 0
    private var flowPositionHandle = 0
    private var flowTimeNoiseColorHandle = 0
    private var flowNoisePos01Handle = 0
    private var flowNoiseTextureHandle = 0
    private var flowPrimaryColorHandle = 0
    private var flowSecondaryColorHandle = 0

    private var bgTextureId = 0
    private var flowTextureId = 0
    private var noiseTextureId = 0

    private var startTime = System.currentTimeMillis()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Load background shader
        val bgVertexShader = CosmicShaderUtil.loadShaderFromAssets(context, "cosmic/background_vertex.glsl", GLES20.GL_VERTEX_SHADER)
        val bgFragmentShader = CosmicShaderUtil.loadShaderFromAssets(context, "cosmic/background_fragment.glsl", GLES20.GL_FRAGMENT_SHADER)
        backgroundProgramId = CosmicShaderUtil.createProgram(bgVertexShader, bgFragmentShader)

        bgPositionHandle = GLES20.glGetAttribLocation(backgroundProgramId, "vPosition")
        bgTexCoordHandle = GLES20.glGetAttribLocation(backgroundProgramId, "texCoord")
        bgTimeHeightScaleColorXOffsetHandle = GLES20.glGetUniformLocation(backgroundProgramId, "u_Time_HeightScale_Color_XOffset")
        bgPrimaryColorHandle = GLES20.glGetUniformLocation(backgroundProgramId, "u_PrimaryColor")
        bgTextureHandle = GLES20.glGetUniformLocation(backgroundProgramId, "sTexture")

        // Load flow shader
        val flowVertexShader = CosmicShaderUtil.loadShaderFromAssets(context, "cosmic/flow_vertex.glsl", GLES20.GL_VERTEX_SHADER)
        val flowFragmentShader = CosmicShaderUtil.loadShaderFromAssets(context, "cosmic/flow_fragment.glsl", GLES20.GL_FRAGMENT_SHADER)
        flowProgramId = CosmicShaderUtil.createProgram(flowVertexShader, flowFragmentShader)

        flowPositionHandle = GLES20.glGetAttribLocation(flowProgramId, "aPosition")
        flowMVPMatrixHandle = GLES20.glGetUniformLocation(flowProgramId, "uMVPMatrix")
        flowTimeNoiseColorHandle = GLES20.glGetUniformLocation(flowProgramId, "u_Time_NoiseScale_Color")
        flowNoisePos01Handle = GLES20.glGetUniformLocation(flowProgramId, "u_NoisePos01")
        flowNoiseTextureHandle = GLES20.glGetUniformLocation(flowProgramId, "sTexture")
        flowPrimaryColorHandle = GLES20.glGetUniformLocation(flowProgramId, "u_PrimaryColor")
        flowSecondaryColorHandle = GLES20.glGetUniformLocation(flowProgramId, "u_SecondaryColor")

        // Load textures
        bgTextureId = CosmicShaderUtil.loadTexture(context, "cosmic/bg_grey.png")
        flowTextureId = CosmicShaderUtil.loadTexture(context, "cosmic/flow_greyscale.png")
        noiseTextureId = CosmicShaderUtil.loadTexture(context, "cosmic/noise.png")

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        matrixState.updateProjection(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val speedMultiplier = currentBpm / 120f
        val time = ((System.currentTimeMillis() - startTime) / 1000.0f) * speedMultiplier

        // 1. Draw Background
        GLES20.glUseProgram(backgroundProgramId)

        // Draw background using the mesh vertices and UV texture coordinates
        mesh.vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(bgPositionHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, mesh.vertexBuffer)
        GLES20.glEnableVertexAttribArray(bgPositionHandle)

        if (bgTexCoordHandle >= 0) {
            mesh.texCoordBuffer.position(0)
            GLES20.glVertexAttribPointer(bgTexCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mesh.texCoordBuffer)
            GLES20.glEnableVertexAttribArray(bgTexCoordHandle)
        }

        // Update background uniforms
        if (bgTimeHeightScaleColorXOffsetHandle >= 0) {
            GLES20.glUniform4f(bgTimeHeightScaleColorXOffsetHandle, time, 1.0f, 0.0f, matrixState.currentParallaxOffset)
        }
        if (bgPrimaryColorHandle >= 0) {
            GLES20.glUniform3fv(bgPrimaryColorHandle, 1, currentTheme.primaryColor, 0)
        }

        if (bgTextureHandle >= 0 && bgTextureId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bgTextureId)
            GLES20.glUniform1i(bgTextureHandle, 0)
        }

        // Note: The mesh goes from -1 to 1, so drawing it without MVP matrix fills the screen
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.indexCount, GLES20.GL_UNSIGNED_SHORT, mesh.indexBuffer)

        if (bgTexCoordHandle >= 0) {
            GLES20.glDisableVertexAttribArray(bgTexCoordHandle)
        }
        GLES20.glDisableVertexAttribArray(bgPositionHandle)

        // 2. Draw Flow Mesh
        GLES20.glUseProgram(flowProgramId)

        matrixState.calculateMVP()
        GLES20.glUniformMatrix4fv(flowMVPMatrixHandle, 1, false, matrixState.mvpMatrix, 0)

        // u_Time_NoiseScale_Color: vec3(time, noiseScale, colorRatio)
        GLES20.glUniform3f(flowTimeNoiseColorHandle, time, currentAmplitude, 0.5f)

        // u_NoisePos01: continuous displacement for noise (vec4)
        GLES20.glUniform4f(flowNoisePos01Handle, time * 0.1f, time * 0.05f, 0.0f, 0.0f)

        // Set theme colors
        GLES20.glUniform3fv(flowPrimaryColorHandle, 1, currentTheme.primaryColor, 0)
        GLES20.glUniform3fv(flowSecondaryColorHandle, 1, currentTheme.secondaryColor, 0)

        // Bind flow greyscale / noise texture
        val activeFlowTexture = if (flowTextureId > 0) flowTextureId else noiseTextureId
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, activeFlowTexture)
        GLES20.glUniform1i(flowNoiseTextureHandle, 0)

        // Enable additive blending
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE)

        mesh.vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(flowPositionHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, mesh.vertexBuffer)
        GLES20.glEnableVertexAttribArray(flowPositionHandle)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.indexCount, GLES20.GL_UNSIGNED_SHORT, mesh.indexBuffer)

        GLES20.glDisableVertexAttribArray(flowPositionHandle)
        GLES20.glDisable(GLES20.GL_BLEND)
    }

    fun setParallaxOffset(offset: Float) {
        matrixState.updateParallax(offset)
    }

    fun setTheme(theme: CosmicTheme) {
        currentTheme = theme
    }

    fun setBpm(bpm: Float) {
        currentBpm = bpm
    }

    fun setAmplitude(amplitude: Float) {
        currentAmplitude = amplitude
    }
}
