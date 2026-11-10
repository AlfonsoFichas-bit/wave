package com.wave.wallpaper.model

data class WaveTheme(
    val clearColor: FloatArray = floatArrayOf(0f, 0f, 0f, 0f),
    val bgc: List<FloatArray> = listOf(
        floatArrayOf(1f, 1f, 1f, 1f),
        floatArrayOf(1f, 1f, 1f, 1f),
        floatArrayOf(1f, 1f, 1f, 1f),
        floatArrayOf(1f, 1f, 1f, 1f)
    ),
    val lc: List<FloatArray> = listOf(
        floatArrayOf(1f, 1f, 1f, 1f),
        floatArrayOf(1f, 1f, 1f, 1f),
        floatArrayOf(1f, 1f, 1f, 1f),
        floatArrayOf(1f, 1f, 1f, 1f),
        floatArrayOf(1f, 1f, 1f, 1f),
        floatArrayOf(1f, 1f, 1f, 1f)
    ),
    val curveAmplitudeRange: FloatArray = floatArrayOf(0.017979452f, 0.21537396f, 0f, 0f),
    val curveFrequencyRange: FloatArray = floatArrayOf(0.26439232f, 1.3595891f, 0f, 0f),
    val curveWidthRange: FloatArray = floatArrayOf(0.04904051f, 0.3219616f, 0f, 0f),
    val curveSpeedRange: FloatArray = floatArrayOf(0.008134329f, 0.0037414383f, 0f, 0f),
    val curveOffsetRange: FloatArray = floatArrayOf(0.26592797f, -0.12153518f, 0f, 0f),
    val curveBendRange: FloatArray = floatArrayOf(0.06823028f, 0.5948827f, 0f, 0f),
    val curveOffsetSpeed: Float = 2.3454159E-4f,
    val curveBendFactor: Float = 0.27292112f,
    val curveBendSpeed: Float = 8.102345E-4f,
    val lineCount: Float = 4.0f,
    val noiseTextureScale: Float = 1.0f,
    val transitions: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WaveTheme

        if (!clearColor.contentEquals(other.clearColor)) return false
        if (bgc.size != other.bgc.size) return false
        for (i in bgc.indices) {
            if (!bgc[i].contentEquals(other.bgc[i])) return false
        }
        if (lc.size != other.lc.size) return false
        for (i in lc.indices) {
            if (!lc[i].contentEquals(other.lc[i])) return false
        }
        if (!curveAmplitudeRange.contentEquals(other.curveAmplitudeRange)) return false
        if (!curveFrequencyRange.contentEquals(other.curveFrequencyRange)) return false
        if (!curveWidthRange.contentEquals(other.curveWidthRange)) return false
        if (!curveSpeedRange.contentEquals(other.curveSpeedRange)) return false
        if (!curveOffsetRange.contentEquals(other.curveOffsetRange)) return false
        if (!curveBendRange.contentEquals(other.curveBendRange)) return false
        if (curveOffsetSpeed != other.curveOffsetSpeed) return false
        if (curveBendFactor != other.curveBendFactor) return false
        if (curveBendSpeed != other.curveBendSpeed) return false
        if (lineCount != other.lineCount) return false
        if (noiseTextureScale != other.noiseTextureScale) return false
        if (transitions != other.transitions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clearColor.contentHashCode()
        for (array in bgc) {
            result = 31 * result + array.contentHashCode()
        }
        for (array in lc) {
            result = 31 * result + array.contentHashCode()
        }
        result = 31 * result + curveAmplitudeRange.contentHashCode()
        result = 31 * result + curveFrequencyRange.contentHashCode()
        result = 31 * result + curveWidthRange.contentHashCode()
        result = 31 * result + curveSpeedRange.contentHashCode()
        result = 31 * result + curveOffsetRange.contentHashCode()
        result = 31 * result + curveBendRange.contentHashCode()
        result = 31 * result + curveOffsetSpeed.hashCode()
        result = 31 * result + curveBendFactor.hashCode()
        result = 31 * result + curveBendSpeed.hashCode()
        result = 31 * result + lineCount.hashCode()
        result = 31 * result + noiseTextureScale.hashCode()
        result = 31 * result + transitions
        return result
    }
}
