# Tasks

## 1. Project Setup

- [ ] 1.1 Create `waves/` directory structure with Gradle, Compose, and verify `./gradlew tasks` succeeds
- [ ] 1.2 Configure `build.gradle.kts` (AGP 8.5+, Kotlin 2.0, Compose enabled) and verify compilation
- [ ] 1.3 Add Gradle wrapper (`gradlew`, `gradlew.bat`) and verify autonomous build
- [ ] 1.4 Create `AndroidManifest.xml` with wallpaper service + activity and verify manifest merges
- [ ] 1.5 Copy assets (shaders, themes, noise.png, previews) from `clone_assets/` to `waves/app/src/main/assets/` and verify files present

## 2. Theme System

- [ ] 2.1 Implement `WaveTheme` data class with all parameters (bgc1-4, lc1-6, ranges, noiseTextureScale, lineCount) and verify default values
- [ ] 2.2 Implement `ThemeParser` tokenizer for `.theme` files and verify parses `lwp.0.theme` correctly
- [ ] 2.3 Add unit test for ThemeParser (lineCount == 4.0 for lwp.0.theme) and verify test passes
- [ ] 2.4 Implement transition parsing from `lwp.-1.theme` and verify variants load
- [ ] 2.5 Implement `PreferencesManager` for theme persistence and verify save/restore

## 3. GL Renderer

- [ ] 3.1 Implement `ShaderUtil` (compile/link shaders, error logging) and verify shaders compile
- [ ] 3.2 Implement `TextureUtil` (load noise.png as GL texture) and verify texture loads
- [ ] 3.3 Implement `WaveRenderer.onSurfaceCreated` (init GL, load shaders + texture) and verify no GL errors
- [ ] 3.4 Implement `WaveRenderer.onDrawFrame` (background + waves render) and verify visual output
- [ ] 3.5 Implement touch handling (touchDown/touch/touchUp) and verify waves respond
- [ ] 3.6 Implement `FramerateController` (30 FPS cap, on-demand, sleep recommendation) and verify FPS limit
- [ ] 3.7 Add GL resource cleanup in `onDestroy` and verify no leaks (LeakCanary or profiler)

## 4. Wallpaper Service

- [ ] 4.1 Implement `WaveWallpaperService` + `WaveEngine` (lifecycle, surface holder) and verify service registers
- [ ] 4.2 Connect `onVisibilityChanged` to pause/resume rendering and verify battery efficiency
- [ ] 4.3 Propagate touch events (`onTouchEvent`, `onCommand`) to renderer and verify interaction
- [ ] 4.4 Implement `WaveGLSurfaceView` (EGL config, translucent, preserve context) and verify EGL init

## 5. Compose UI

- [ ] 5.1 Create `MainActivity` with Material 3 theme and verify launch
- [ ] 5.2 Implement `WallpaperPreview` (AndroidView + GLSurfaceView) and verify live preview
- [ ] 5.3 Implement `ColorSelector` grid with 16 themes and thumbnails and verify selection
- [ ] 5.4 Implement "Apply" button opening `WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER` and verify picker opens
- [ ] 5.5 Wire theme selection to preview and persistence and verify end-to-end

## 6. Integration & Validation

- [ ] 6.1 Build debug APK (`./gradlew assembleDebug`) and verify no errors
- [ ] 6.2 Install on emulator and verify wallpaper appears in picker
- [ ] 6.3 Apply wallpaper and verify rendering at 60 FPS
- [ ] 6.4 Test touch interaction and theme switching on device
- [ ] 6.5 Verify no `UnsatisfiedLinkError` or native dependency issues
- [ ] 6.6 Run linter and fix warnings

## Workflow follow-up

- Archive the change after review requirements are satisfied.
- Verify the archived result.
PY