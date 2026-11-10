package com.buga.cosmic.gl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class CosmicMesh(private val resolutionX: Int = 48, private val resolutionY: Int = 48) {

    val vertexBuffer: FloatBuffer
    val indexBuffer: ShortBuffer
    val indexCount: Int

    init {
        // Create vertices
        // vec4 for position: x, y, z=0.0, w=1.0
        val numVertices = resolutionX * resolutionY
        val vertexData = FloatArray(numVertices * 4)

        var vertexIndex = 0
        for (y in 0 until resolutionY) {
            for (x in 0 until resolutionX) {
                // Map from [0, resolution-1] to [-1.0, 1.0]
                val xPos = (x.toFloat() / (resolutionX - 1)) * 2.0f - 1.0f
                val yPos = (y.toFloat() / (resolutionY - 1)) * 2.0f - 1.0f

                vertexData[vertexIndex++] = xPos
                vertexData[vertexIndex++] = yPos
                vertexData[vertexIndex++] = 0.0f // z
                vertexData[vertexIndex++] = 1.0f // w
            }
        }

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertexData)
        vertexBuffer.position(0)

        // Create indices for GL_TRIANGLES
        // For a grid of (resolutionX) by (resolutionY) vertices,
        // we have (resolutionX - 1) by (resolutionY - 1) quads.
        // Each quad is 2 triangles = 6 indices.
        val numQuads = (resolutionX - 1) * (resolutionY - 1)
        indexCount = numQuads * 6
        val indexData = ShortArray(indexCount)

        var indexIndex = 0
        for (y in 0 until resolutionY - 1) {
            for (x in 0 until resolutionX - 1) {
                val topLeft = (y * resolutionX + x).toShort()
                val topRight = (y * resolutionX + x + 1).toShort()
                val bottomLeft = ((y + 1) * resolutionX + x).toShort()
                val bottomRight = ((y + 1) * resolutionX + x + 1).toShort()

                // Triangle 1: Top-Left, Bottom-Left, Top-Right
                indexData[indexIndex++] = topLeft
                indexData[indexIndex++] = bottomLeft
                indexData[indexIndex++] = topRight

                // Triangle 2: Top-Right, Bottom-Left, Bottom-Right
                indexData[indexIndex++] = topRight
                indexData[indexIndex++] = bottomLeft
                indexData[indexIndex++] = bottomRight
            }
        }

        indexBuffer = ByteBuffer.allocateDirect(indexData.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexBuffer.put(indexData)
        indexBuffer.position(0)
    }

    fun bindAndDraw() {
        // Not binding VBOs here, assuming simpler client-side vertex arrays
        // as per GLES20 standard usage without VBOs for simplicity,
        // though VBOs could be added.
    }
}
