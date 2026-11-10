package com.wave.wallpaper.model

import android.content.Context
import java.io.InputStreamReader

object ThemeLoader {
    fun loadTheme(context: Context, index: Int): WaveTheme {
        return try {
            val content = readAsset(context, "lwp.$index.theme")
            ThemeParser.parse(content)
        } catch (e: Exception) {
            try {
                val content = readAsset(context, "lwp.-2.theme")
                ThemeParser.parse(content)
            } catch (e2: Exception) {
                WaveTheme()
            }
        }
    }

    private fun readAsset(context: Context, fileName: String): String {
        return context.assets.open(fileName).use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                reader.readText()
            }
        }
    }
}
