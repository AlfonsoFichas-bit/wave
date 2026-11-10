package com.wave.wallpaper.model

object ThemeParser {
    fun parse(content: String): WaveTheme {
        val tokens = content.split("\\s+".toRegex()).filter { it.isNotBlank() }
        var i = 0

        var clearColor = floatArrayOf(0f, 0f, 0f, 0f)
        val bgc = MutableList(4) { floatArrayOf(1f, 1f, 1f, 1f) }
        val lc = MutableList(6) { floatArrayOf(1f, 1f, 1f, 1f) }
        var curveAmplitudeRange = floatArrayOf(0.017979452f, 0.21537396f, 0f, 0f)
        var curveFrequencyRange = floatArrayOf(0.26439232f, 1.3595891f, 0f, 0f)
        var curveWidthRange = floatArrayOf(0.04904051f, 0.3219616f, 0f, 0f)
        var curveSpeedRange = floatArrayOf(0.008134329f, 0.0037414383f, 0f, 0f)
        var curveOffsetRange = floatArrayOf(0.26592797f, -0.12153518f, 0f, 0f)
        var curveBendRange = floatArrayOf(0.06823028f, 0.5948827f, 0f, 0f)
        var curveOffsetSpeed = 2.3454159E-4f
        var curveBendFactor = 0.27292112f
        var curveBendSpeed = 8.102345E-4f
        var lineCount = 4.0f
        var noiseTextureScale = 1.0f
        var transitions = 0

        fun parseFloats(count: Int): FloatArray {
            val result = FloatArray(count)
            for (j in 0 until count) {
                if (i < tokens.size) {
                    result[j] = tokens[i++].toFloatOrNull() ?: 0f
                }
            }
            return result
        }

        while (i < tokens.size) {
            val key = tokens[i++]
            when (key) {
                "clearColor" -> clearColor = parseFloats(4)
                "bgc1" -> bgc[0] = parseFloats(4)
                "bgc2" -> bgc[1] = parseFloats(4)
                "bgc3" -> bgc[2] = parseFloats(4)
                "bgc4" -> bgc[3] = parseFloats(4)
                "lc1" -> lc[0] = parseFloats(4)
                "lc2" -> lc[1] = parseFloats(4)
                "lc3" -> lc[2] = parseFloats(4)
                "lc4" -> lc[3] = parseFloats(4)
                "lc5" -> lc[4] = parseFloats(4)
                "lc6" -> lc[5] = parseFloats(4)
                "curveAmplitudeRange" -> curveAmplitudeRange = parseFloats(4)
                "curveFrequencyRange" -> curveFrequencyRange = parseFloats(4)
                "curveWidthRange" -> curveWidthRange = parseFloats(4)
                "curveSpeedRange" -> curveSpeedRange = parseFloats(4)
                "curveOffsetRange" -> curveOffsetRange = parseFloats(4)
                "curveBendRange" -> curveBendRange = parseFloats(4)
                "curveOffsetSpeed" -> curveOffsetSpeed = parseFloats(1).firstOrNull() ?: curveOffsetSpeed
                "curveBendFactor" -> curveBendFactor = parseFloats(1).firstOrNull() ?: curveBendFactor
                "curveBendSpeed" -> curveBendSpeed = parseFloats(1).firstOrNull() ?: curveBendSpeed
                "lineCount" -> lineCount = parseFloats(1).firstOrNull() ?: lineCount
                "noiseTextureScale" -> noiseTextureScale = parseFloats(1).firstOrNull() ?: noiseTextureScale
                ".transitions" -> transitions = parseFloats(1).firstOrNull()?.toInt() ?: transitions
                else -> {
                    // Ignore other properties for now
                }
            }
        }

        return WaveTheme(
            clearColor = clearColor,
            bgc = bgc,
            lc = lc,
            curveAmplitudeRange = curveAmplitudeRange,
            curveFrequencyRange = curveFrequencyRange,
            curveWidthRange = curveWidthRange,
            curveSpeedRange = curveSpeedRange,
            curveOffsetRange = curveOffsetRange,
            curveBendRange = curveBendRange,
            curveOffsetSpeed = curveOffsetSpeed,
            curveBendFactor = curveBendFactor,
            curveBendSpeed = curveBendSpeed,
            lineCount = lineCount,
            noiseTextureScale = noiseTextureScale,
            transitions = transitions
        )
    }
}
