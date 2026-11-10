package com.wave.wallpaper

import android.service.wallpaper.WallpaperService

class WaveWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return WaveEngine()
    }

    inner class WaveEngine : Engine() {
        // Minimal engine stub
    }
}
