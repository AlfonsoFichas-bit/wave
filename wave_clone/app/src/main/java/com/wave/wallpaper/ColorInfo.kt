package com.wave.wallpaper

import android.content.Context
import android.content.SharedPreferences

data class ColorInfo(
    val nameRes: Int,
    val index: Int,
    val thumbnailRes: Int,
    val label: String
) {
    companion object {
        val Wave = ColorInfo(0, -1, 0, "Wave")
        val Blue = ColorInfo(0, 0, 0, "Blue")
        val Purple = ColorInfo(0, 1, 0, "Purple")
        val Red = ColorInfo(0, 2, 0, "Red")
        val Orange = ColorInfo(0, 3, 0, "Orange")
        val Green = ColorInfo(0, 4, 0, "Green")
        val Gray = ColorInfo(0, 5, 0, "Gray")
        val Black = ColorInfo(0, 6, 0, "Black")
        val all = listOf(Wave, Blue, Purple, Red, Orange, Green, Gray, Black)
    }
}

class ColorPicker(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(WaveRenderer.PREFS_NAME, 0)
    private var currentIndex: Int = prefs.getInt(WaveRenderer.PREFS_KEY_COLOR, 0)

    init {
        prefs.registerOnSharedPreferenceChangeListener { sp, key ->
            if (key == WaveRenderer.PREFS_KEY_COLOR) {
                currentIndex = sp.getInt(WaveRenderer.PREFS_KEY_COLOR, 0)
            }
        }
    }

    fun getCurrentIndex(): Int = currentIndex
    fun destroy() { /* unregister if needed */ }
}
PY