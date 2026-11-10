package com.wave.wallpaper.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wave.wallpaper.model.ThemeLoader
import com.wave.wallpaper.model.WaveTheme

@Composable
fun ThemeSelector(
    selectedThemeIndex: Int,
    onThemeSelected: (Int, WaveTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Load all 16 themes efficiently in memory for thumbnails
    val themes = remember {
        List(16) { index ->
            ThemeLoader.loadTheme(context, index)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(80.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(16) { index ->
            val theme = themes[index]
            val isSelected = index == selectedThemeIndex

            ThemeItem(
                theme = theme,
                name = "Theme $index",
                isSelected = isSelected,
                onClick = { onThemeSelected(index, theme) }
            )
        }
    }
}

@Composable
private fun ThemeItem(
    theme: WaveTheme,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                    shape = CircleShape
                )
        ) {
            // Generate simple thumbnail using bgc1
            val bgColorArray = theme.bgc.firstOrNull() ?: floatArrayOf(0f, 0f, 0f, 1f)
            val bgColor = Color(
                red = bgColorArray.getOrNull(0) ?: 0f,
                green = bgColorArray.getOrNull(1) ?: 0f,
                blue = bgColorArray.getOrNull(2) ?: 0f,
                alpha = bgColorArray.getOrNull(3) ?: 1f
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = bgColor)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
