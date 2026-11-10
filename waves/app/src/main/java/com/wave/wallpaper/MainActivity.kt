package com.wave.wallpaper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wave.wallpaper.model.ThemeLoader
import com.wave.wallpaper.ui.ThemeSelector
import com.wave.wallpaper.ui.WallpaperPreview
import com.wave.wallpaper.ui.WaveAppTheme
import com.wave.wallpaper.util.PreferencesManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WaveAppTheme {
                WaveApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaveApp() {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }

    var selectedThemeIndex by remember { mutableIntStateOf(prefsManager.loadThemeIndex()) }
    var currentTheme by remember {
        mutableStateOf(ThemeLoader.loadTheme(context, selectedThemeIndex))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wave") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                WallpaperPreview(theme = currentTheme)
            }

            ThemeSelector(
                selectedThemeIndex = selectedThemeIndex,
                onThemeSelected = { index, theme ->
                    selectedThemeIndex = index
                    currentTheme = theme
                    prefsManager.saveThemeIndex(index)
                },
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                            putExtra(
                                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                ComponentName(context, WaveWallpaperService::class.java)
                            )
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Handle if the device doesn't support live wallpapers
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply Wallpaper")
                }
            }
        }
    }
}
