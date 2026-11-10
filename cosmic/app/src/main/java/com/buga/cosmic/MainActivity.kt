package com.buga.cosmic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buga.cosmic.theme.CosmicTheme
import com.buga.cosmic.ui.CosmicFlowVisualizer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CosmicShowcaseScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosmicShowcaseScreen() {
    var selectedTheme by remember { mutableStateOf(CosmicTheme.DEFAULT) }
    var bpm by remember { mutableFloatStateOf(120f) }
    var amplitude by remember { mutableFloatStateOf(1.0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full-screen background visualizer
        CosmicFlowVisualizer(
            modifier = Modifier.fillMaxSize(),
            theme = selectedTheme,
            bpm = bpm,
            amplitude = amplitude
        )

        // Top Bar (Minimalist glassmorphic / dark)
        TopAppBar(
            title = {
                Text(
                    text = "Cosmic Flow 3D",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black.copy(alpha = 0.3f),
                titleContentColor = Color.White
            )
        )

        // Bottom Control Panel
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp), // Extra padding for system nav bar
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.6f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Theme Selector
                Text(
                    text = "Theme Palette",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(CosmicTheme.getAllThemes()) { theme ->
                        ThemeChip(
                            theme = theme,
                            isSelected = selectedTheme == theme,
                            onClick = { selectedTheme = theme }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // BPM Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BPM (${bpm.toInt()})",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.width(80.dp)
                    )
                    Slider(
                        value = bpm,
                        onValueChange = { bpm = it },
                        valueRange = 60f..180f,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Amplitude Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Amplitude (${String.format("%.1f", amplitude)})",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.width(80.dp)
                    )
                    Slider(
                        value = amplitude,
                        onValueChange = { amplitude = it },
                        valueRange = 0.5f..2.0f,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Drag hint
                Text(
                    text = "Swipe horizontally to tilt the 3D ribbons in space",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ThemeChip(
    theme: CosmicTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.White) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(theme.thumbnailColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = theme.displayName,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}
