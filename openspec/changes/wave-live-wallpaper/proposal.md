# Proposal

## Why

El fondo de pantalla "Experience Flow" de Sony Xperia (2013) es un live wallpaper icónico basado en OpenGL ES 2.0 y librerías nativas de 32 bits (armeabi-v7a) que ya no son compatibles con dispositivos Android modernos (arm64-v8a). Además, su UI de configuración está obsoleta. Se necesita reimplementarlo de forma 1:1 en Kotlin puro (sin dependencias nativas) y modernizar la experiencia con Jetpack Compose + Material 3, manteniendo el renderizado a 60/120 FPS con bajo consumo de batería.

## What Changes

- Nuevo proyecto Android `waves/` con arquitectura híbrida: Jetpack Compose (UI) + OpenGL ES 2.0 (wallpaper).
- Implementación de `WaveWallpaperService` y `WaveEngine` conectando el ciclo de vida del wallpaper con el renderer GL.
- Motor gráfico en Kotlin puro (`WaveRenderer`) con shaders GLSL portados (`gfxbackground`, `gfxline`) y textura de ruido (`noise.png`).
- Parser de temas (`ThemeParser`) para los 16 archivos `lwp.*.theme` + transiciones de `lwp.-1.theme`.
- Modelo de datos `WaveTheme` con valores por defecto seguros.
- UI en Compose: `MainActivity`, selector de 16 temas, preview en vivo con `AndroidView` + `GLSurfaceView`, botón para aplicar via `WallpaperManager`.
- Persistencia de tema elegido en SharedPreferences.
- Propagación de toques (POINTER_DOWN/MOVE/UP, ACCELERATE) y eficiencia energética (pausa en `onVisibilityChanged(false)`).
- Copia de assets (shaders, temas, noise.png, previews) desde el APK original extraído.

## Capabilities

### New Capabilities

- `live-wallpaper-service`: Servicio de live wallpaper (`WaveWallpaperService`, `WaveEngine`, lifecycle, touch propagation, eficiencia energética).
- `gl-wave-renderer`: Motor gráfico OpenGL ES 2.0 en Kotlin puro (shaders, textura de ruido, geometría, render loop, interactividad táctil, framerate).
- `theme-system`: Parser de archivos `.theme` y modelo `WaveTheme` (16 temas + transiciones).
- `compose-wallpaper-ui`: Interfaz en Jetpack Compose (Material 3) con selector de temas, preview en vivo y aplicación del wallpaper.

### Modified Capabilities

(Ninguna — proyecto nuevo.)

## Impact

- Código nuevo en `waves/` (proyecto Android independiente).
- Assets reutilizados desde el APK extraído (`clone_assets/`).
- Dependencias: AndroidX Compose, Material 3, OpenGL ES 2.0 (built-in).
- Sin dependencias nativas (elimina `liblwplocal.so` de 32 bits).
- Compatible con Android 5.0+ (minSdk 21), targetSdk 34.
PY