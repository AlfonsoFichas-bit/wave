package com.wave.wallpaper.gl

import android.opengl.GLES20
import android.util.Log

object ShaderUtil {
    private const val TAG = "ShaderUtil"

    const val BG_VERTEX_SHADER = """
        attribute vec2 a_position;
        varying vec2 v_pos;
        void main() {
            v_pos = a_position;
            gl_Position = vec4(a_position, 0.5, 1.0);
        }
    """

    const val BG_FRAGMENT_SHADER = """
        precision mediump float;
        varying vec2 v_pos;
        uniform float u_time;
        uniform float u_parallaxOffset;
        uniform float u_noiseScale;
        uniform vec4 u_bgc1;
        uniform vec4 u_bgc2;
        uniform vec4 u_bgc3;
        uniform vec4 u_bgc4;
        uniform sampler2D u_noiseTex;
        uniform vec2 u_resolution;

        // Sony Experience Flow wave motion for the background
        float baseMotion(vec2 p, float t) {
            float offset = cos(t * 0.15) * 0.15;
            float bend = sin(t * 0.25 + p.x * 2.0) * 0.12;
            return offset + bend;
        }

        void main() {
            vec2 p = v_pos;
            vec2 ild = p;
            ild.x += (u_parallaxOffset - 0.5) * 0.4;
            
            vec2 ld = p;
            ld.x += (u_parallaxOffset - 0.5) * 0.6;
            ld.y += baseMotion(ld, u_time);
            
            float aspect = u_resolution.y / max(u_resolution.x, 1.0);
            float loc = clamp(ld.y * 2.0 + 0.5, 0.0, 1.0);
            float po = min(length(ild * vec2(1.0, 1.0 / aspect) * 0.5), 1.0);
            
            vec2 noiseUV = (ild * 0.5 + 0.5) * u_noiseScale;
            vec4 topCol = mix(u_bgc2, u_bgc1, po);
            vec4 btmCol = mix(u_bgc3, u_bgc4, po);
            vec4 col = mix(btmCol, topCol, loc);
            
            vec4 noiseVal = texture2D(u_noiseTex, noiseUV);
            col.rgb += (noiseVal.rgb - 0.5) * 0.12;
            
            gl_FragColor = col;
        }
    """

    const val LINE_VERTEX_SHADER = """
        attribute vec2 a_position;
        attribute float a_alpha;
        varying float v_alpha;
        void main() {
            v_alpha = a_alpha;
            gl_Position = vec4(a_position, 0.0, 1.0);
        }
    """

    const val LINE_FRAGMENT_SHADER = """
        precision mediump float;
        uniform vec4 u_color;
        varying float v_alpha;
        void main() {
            gl_FragColor = vec4(u_color.rgb, u_color.a * v_alpha);
        }
    """

    fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader $type:\n" + GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) return 0

        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (fragmentShader == 0) {
            GLES20.glDeleteShader(vertexShader)
            return 0
        }

        val program = GLES20.glCreateProgram()
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program:\n" + GLES20.glGetProgramInfoLog(program))
                GLES20.glDeleteProgram(program)
                return 0
            }
        }
        return program
    }
}
