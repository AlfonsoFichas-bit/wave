package com.wave.wallpaper.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("WavePrefs", Context.MODE_PRIVATE)

    fun saveThemeIndex(index: Int) {
        prefs.edit().putInt("preferences_color", index).apply()
    }

    fun loadThemeIndex(): Int {
        return prefs.getInt("preferences_color", 0)
    }
}
