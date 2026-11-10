package com.buga.cosmic.gl

import android.opengl.Matrix

class CosmicMatrixState {

    val mvpMatrix = FloatArray(16)

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private val tempMatrix = FloatArray(16)

    private var currentParallaxOffset = 0f

    init {
        Matrix.setIdentityM(modelMatrix, 0)

        // Tilt the model slightly to simulate flying perspective
        Matrix.rotateM(modelMatrix, 0, 15f, 1f, 0f, 0f)
    }

    fun updateProjection(width: Int, height: Int) {
        val ratio = width.toFloat() / height.toFloat()

        // Perspective projection: FOV ~45°, near 0.1f, far 100f
        // Matrix.frustumM can be used. Left, right, bottom, top need calculation
        // Or simply use perspectiveM if available (API 14+)
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 100f)
    }

    fun updateView() {
        // Camera setup
        Matrix.setLookAtM(
            viewMatrix, 0,
            0f, -1.5f, 2.2f, // Eye
            0f, 0f, 0f,      // Center
            0f, 1f, 0f       // Up
        )

        // Apply parallax to view (or model, depending on preference)
        // Here we apply a rotation around Y axis to the view/model based on parallax
        Matrix.rotateM(viewMatrix, 0, currentParallaxOffset * 10f, 0f, 1f, 0f)
    }

    fun updateParallax(xOffset: Float) {
        // Smoothly update parallax based on swipe offset (0.0 to 1.0 generally)
        // Map [0, 1] to [-1, 1]
        currentParallaxOffset = (xOffset - 0.5f) * 2.0f
    }

    fun calculateMVP() {
        updateView()

        // tempMatrix = view * model
        Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)

        // mvp = proj * tempMatrix
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)
    }
}
