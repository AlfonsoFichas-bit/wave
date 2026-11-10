# Wave — Live Wallpaper (clon de Sony "Experience Flow")

## Objetivo

Reimplementar el live wallpaper "Experience Flow" de Sony (package `com.sonymobile.experienceflow2`) como una app llamada **Wave**, preservando:
- Renderizado de ondas/curvas con OpenGL ES 2.0 (fondo animado + líneas).
- Sistema de temas (`.theme`) con 16 variantes + transiciones.
- Interacción táctil (touchDown/touch/touchUp) y aceleración.
- Selector de color y preferencias.
- Control de framerate (on-demand / continuo) y sleep recommendation.

## ¿Jetpack Compose?

**No para el wallpaper en sí.** El renderizado del fondo de pantalla se hace en un `WallpaperService` + `GLSurfaceView` con OpenGL ES 2.0 (como en el original). Jetpack Compose no puede reemplazar ese pipeline GL.

**Sí para la UI de configuración.** La `PreferencesActivity` puede implementarse con Compose (Material 3) para el selector de temas/colores y ajustes. En este esqueleto se usa una Activity simple con ListView para mantener paridad con el original; se puede migrar a Compose sin afectar el core GL.

## Estructura del proyecto

```
wave_clone/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/wave/wallpaper/
│   │   ├── WaveWallpaperService.kt   # WallpaperService + Engine + GLSurfaceView
│   │   ├── WaveRenderer.kt           # GLSurfaceView.Renderer
│   │   ├── WaveCurveRenderer.kt      # JNI bridge a liblwplocal.so (o port Kotlin)
│   │   ├── WavePreferencesActivity.kt
│   │   ├── ColorInfo.kt / ColorPicker.kt
│   │   ├── FramerateController.kt    # control de FPS
│   │   ├── FreeList.kt / SwipeData.kt
│   ├── assets/                       # shaders, temas, noise.png (copiados del APK)
│   └── res/                          # xml/wallpaper.xml, strings
├── build.gradle.kts / settings.gradle.kts
```

## Assets

Los assets originales (shaders `gfxbackground.*`, `gfxline*`, temas `lwp.*.theme`, `noise.png`) están en `app/src/main/assets/`. Fueron extraídos del APK con REA (`extract_artifact`) y copiados aquí.

## Renderer nativo vs Kotlin puro

- **Opción A (máxima fidelidad/performance):** compilar `liblwplocal.so` para arm64-v8a/x86_64 y usar el JNI bridge (`WaveCurveRenderer`).
- **Opción B (portable, sin native):** implementar el render en Kotlin con GLES20 siguiendo el esqueleto de `CODIGO_DECOMPILADO_WAVE.md` §8.4, portando los shaders GLSL.

## Claves de preferencias

- `WavePrefs` / `preferences_color` (int, índice de color).

## Referencias

- `../ANALISIS_WAVE.md` — hallazgos + plan.
- `../CODIGO_DECOMPILADO_WAVE.md` — código real decompilado + pseudocódigo exacto.
- `../reference_java/` — 41 archivos Java decompilados.
- `../clone_assets/` — assets extraídos del APK.

## Build

Abrir `wave_clone/` en Android Studio (Giraffe+), sincronizar Gradle y ejecutar en emulador/dispositivo.

> Nota: el render nativo requiere recompilar `liblwplocal.so` para las ABIs objetivo. Sin eso, usar la opción B (Kotlin puro).
PY