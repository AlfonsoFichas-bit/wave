package com.wave.wallpaper

import com.wave.wallpaper.model.ThemeParser
import com.wave.wallpaper.model.WaveTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.InputStreamReader

class ThemeParserTest {

    private fun readResource(fileName: String): String {
        return javaClass.classLoader?.getResourceAsStream(fileName)?.use { inputStream ->
            InputStreamReader(inputStream).use { it.readText() }
        } ?: throw IllegalArgumentException("Resource not found: $fileName")
    }

    @Test
    fun testParseLwp0Theme() {
        val content = readResource("lwp.0.theme")
        val theme = ThemeParser.parse(content)

        assertEquals(4.0f, theme.lineCount)
        assertNotNull(theme)
    }

    @Test
    fun testParseLwpMinus2Theme() {
        val content = readResource("lwp.-2.theme")
        val theme = ThemeParser.parse(content)

        assertNotNull(theme)
    }

    @Test
    fun testEmptyThemeUsesDefaults() {
        val theme = ThemeParser.parse("")
        val defaultTheme = WaveTheme()

        assertEquals(defaultTheme, theme)
    }
}
