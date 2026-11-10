package com.buga.cosmic.gl

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log
import java.io.IOException
import java.nio.charset.Charset

object CosmicShaderUtil {

    private const val TAG = "CosmicShaderUtil"

    /**
     * Loads a shader from the assets folder.
     */
    fun loadShaderFromAssets(context: Context, assetPath: String, type: Int): Int {
        val shaderCode = try {
            val inputStream = context.assets.open(assetPath)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            Log.e(TAG, "Error reading shader from assets: $assetPath", e)
            return 0
        }

        val shaderId = GLES20.glCreateShader(type)
        if (shaderId == 0) {
            Log.e(TAG, "Error creating shader type: $type")
            return 0
        }

        GLES20.glShaderSource(shaderId, shaderCode)
        GLES20.glCompileShader(shaderId)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val errorMsg = GLES20.glGetShaderInfoLog(shaderId)
            Log.e(TAG, "Error compiling shader: $assetPath\n$errorMsg")
            GLES20.glDeleteShader(shaderId)
            return 0
        }

        return shaderId
    }

    /**
     * Creates an OpenGL program from vertex and fragment shaders.
     */
    fun createProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programId = GLES20.glCreateProgram()
        if (programId == 0) {
            Log.e(TAG, "Error creating program")
            return 0
        }

        GLES20.glAttachShader(programId, vertexShaderId)
        GLES20.glAttachShader(programId, fragmentShaderId)
        GLES20.glLinkProgram(programId)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val errorMsg = GLES20.glGetProgramInfoLog(programId)
            Log.e(TAG, "Error linking program:\n$errorMsg")
            GLES20.glDeleteProgram(programId)
            return 0
        }

        return programId
    }

    /**
     * Loads a texture from the assets folder.
     */
    fun loadTexture(context: Context, assetPath: String): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)

        if (textureIds[0] == 0) {
            Log.e(TAG, "Error generating texture ID for $assetPath")
            return 0
        }

        val options = BitmapFactory.Options()
        options.inScaled = false // Don't scale based on density

        val bitmap = try {
            val inputStream = context.assets.open(assetPath)
            BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: IOException) {
            Log.e(TAG, "Error loading texture from assets: $assetPath", e)
            GLES20.glDeleteTextures(1, textureIds, 0)
            return 0
        }

        if (bitmap == null) {
            Log.e(TAG, "Error decoding bitmap from: $assetPath")
            GLES20.glDeleteTextures(1, textureIds, 0)
            return 0
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        // Set wrapping
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle()

        // Unbind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        return textureIds[0]
    }
}
