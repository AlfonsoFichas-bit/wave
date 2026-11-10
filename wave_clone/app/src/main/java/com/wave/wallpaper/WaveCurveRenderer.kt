package com.wave.wallpaper

import android.content.res.AssetManager

/**
 * JNI bridge hacia liblwplocal.so (renderer nativo).
 * Si no se porta la lib nativa, implementar en Kotlin puro (ver CODIGO_DECOMPILADO_WAVE.md §8.4).
 */
abstract class WaveCurveRenderer {
    protected var id: Int = 0

    protected external fun activate(id: Int)
    protected external fun deactivate(id: Int)
    protected external fun getSleepRecommendation(id: Int): Int
    protected external fun init(assetManager: AssetManager): Int
    protected external fun render(id: Int, assetManager: AssetManager)
    protected external fun requestColors(id: Int, assetManager: AssetManager, theme: Int)
    protected external fun touch(id: Int, x: Float, y: Float)
    protected external fun touchDown(id: Int, x: Float, y: Float)
    protected external fun touchUp(id: Int, x: Float, y: Float)
    protected external fun updateScreenLayout(id: Int, x: Int, y: Int, w: Int, h: Int, sw: Int, sh: Int)

    fun activate() { if (id == 0) id = init(assetManagerRef); activate(id) }
    fun deactivate() { if (id != 0) deactivate(id) }
    fun getSleepRecommendation(): Int = if (id != 0) getSleepRecommendation(id) else 0
    fun render(am: AssetManager) { if (id != 0) render(id, am) }
    fun requestColors(am: AssetManager, theme: Int) { if (id != 0) requestColors(id, am, theme) }
    fun touch(x: Float, y: Float) { if (id != 0) touch(id, x, y) }
    fun touchDown(x: Float, y: Float) { if (id != 0) touchDown(id, x, y) }
    fun touchUp(x: Float, y: Float) { if (id != 0) touchUp(id, x, y) }
    fun updateScreenLayout(x: Int, y: Int, w: Int, h: Int, sw: Int, sh: Int) {
        if (id != 0) updateScreenLayout(id, x, y, w, h, sw, sh)
    }

    // Referencia al AssetManager se inyecta en el constructor concreto
    protected lateinit var assetManagerRef: AssetManager

    companion object {
        init { System.loadLibrary("lwplocal") }
    }
}

class WaveCurveRendererImpl(am: AssetManager) : WaveCurveRenderer() {
    init { assetManagerRef = am; id = init(am) }
}
PY