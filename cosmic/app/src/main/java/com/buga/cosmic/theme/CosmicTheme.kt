package com.buga.cosmic.theme

import androidx.compose.ui.graphics.Color

enum class CosmicTheme(
    val primaryColor: FloatArray,
    val secondaryColor: FloatArray,
    val displayName: String,
    val thumbnailColor: Color
) {
    AMETHYST(
        floatArrayOf(0.55f, 0.12f, 0.72f),
        floatArrayOf(0.85f, 0.25f, 0.95f),
        "Amethyst",
        Color(0.55f, 0.12f, 0.72f)
    ),
    TURQUOISE(
        floatArrayOf(0.05f, 0.45f, 0.58f),
        floatArrayOf(0.20f, 0.80f, 0.85f),
        "Turquoise",
        Color(0.05f, 0.45f, 0.58f)
    ),
    EMERALD(
        floatArrayOf(0.08f, 0.48f, 0.22f),
        floatArrayOf(0.30f, 0.85f, 0.40f),
        "Emerald",
        Color(0.08f, 0.48f, 0.22f)
    ),
    SAPPHIRE(
        floatArrayOf(0.08f, 0.25f, 0.68f),
        floatArrayOf(0.25f, 0.55f, 0.95f),
        "Sapphire",
        Color(0.08f, 0.25f, 0.68f)
    ),
    GOLD(
        floatArrayOf(0.75f, 0.48f, 0.05f),
        floatArrayOf(0.98f, 0.80f, 0.20f),
        "Gold",
        Color(0.75f, 0.48f, 0.05f)
    ),
    SILK(
        floatArrayOf(0.52f, 0.45f, 0.48f),
        floatArrayOf(0.88f, 0.75f, 0.78f),
        "Silk",
        Color(0.52f, 0.45f, 0.48f)
    ),
    RUBY(
        floatArrayOf(0.68f, 0.08f, 0.15f),
        floatArrayOf(0.95f, 0.35f, 0.25f),
        "Ruby",
        Color(0.68f, 0.08f, 0.15f)
    );

    companion object {
        val DEFAULT = AMETHYST
        fun getAllThemes(): List<CosmicTheme> = entries
    }
}
