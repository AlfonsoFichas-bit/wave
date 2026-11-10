# Design

## Context

See `proposal.md` for motivation. The original Sony "Experience Flow" (2013) uses `liblwplocal.so` (32-bit ARM, armeabi-v7a) with OpenGL ES 2.0 and a C++ renderer. This library is incompatible with modern 64-bit devices. The decompiled Java (jadx) reveals the full architecture: `GLWallpaperService` → `GLEngine` → `WallpaperGLSurfaceView` (EGL RGBA8, translucent) → `ExperienceFlowRenderer` → `CurveRendererImpl` (JNI). The rendering parameters are driven by `.theme` text files parsed by `readTheme`. The UI was a basic `PreferenceActivity`. All of this is documented in `ANALISIS_WAVE.md` and `CODIGO_DECOMPILADO_WAVE.md`.

## Goals / Non-Goals

**Goals:**
- Reproduce the visual output and interaction of the original at 60/120 FPS.
- Eliminate native 32-bit dependencies by porting the renderer to Kotlin + GLES20.
- Modernize configuration UI with Jetpack Compose + Material 3.
- Maintain energy efficiency (pause on invisibility, 30 FPS cap, on-demand rendering).

**Non-Goals:**
- Porting the native C++ library (Option A) — deferred; Kotlin puro (Option B) is the primary path.
- Supporting Sony-specific lockscreen commands (`POINTER_DOWN/MOVE/UP`, `ACCELERATE`) — replaced by standard touch events.
- Supporting runtime skinning (Sony `RuntimeSkinning`) — omitted; Material You dynamic colors may be added later.
- Analytics/tracking from the original (Google Analytics, UA-51142570-8) — omitted.

## Decisions

### D1: Kotlin puro + GLES20 instead of native C++

- **Choice**: Port the renderer to Kotlin using OpenGL ES 2.0 directly, reusing the original GLSL shaders.
- **Rationale**: Eliminates `UnsatisfiedLinkError` on 64-bit devices, simplifies build (no NDK), and allows easier maintenance. The shaders are portable GLSL ES 2.0.
- **Alternative**: Recompile `liblwplocal.so` for arm64-v8a/x86_64. Rejected due to NDK complexity and STL dependency (gnustl).
- **Trade-off**: Potential performance difference; mitigated by GPU-based rendering and 30 FPS cap.

### D2: Jetpack Compose for UI, GLSurfaceView for wallpaper

- **Choice**: Compose for `MainActivity` and settings; `GLSurfaceView` (via `AndroidView`) for the live preview and wallpaper service.
- **Rationale**: Compose provides modern, animated UI. GLSurfaceView is required for OpenGL ES rendering in a wallpaper context.
- **Alternative**: XML layouts. Rejected — less maintainable, no dynamic theming.

### D3: Shared renderer for preview and wallpaper

- **Choice**: Use a single `WaveRenderer` instance shared between the preview `GLSurfaceView` and the wallpaper service's `GLSurfaceView`.
- **Rationale**: Ensures visual consistency; the preview shows exactly what the wallpaper will look like.
- **Risk**: EGL context sharing complexity. Mitigation: use separate `GLSurfaceView` instances with the same renderer logic but independent contexts; share only theme data and shader source.

### D4: Theme parser as text tokenizer

- **Choice**: Parse `.theme` files by splitting on whitespace and reading key + 4 floats, matching the C++ `readTheme` behavior.
- **Rationale**: Faithful to the original format; handles all parameter types (colors as RGBA, ranges as 4 floats).
- **Edge cases**: Missing parameters → defaults; transition variants (`.0`, `.1`) parsed separately.

### D5: Framerate control with Handler-based scheduling

- **Choice**: Port `FramerateControllerBase` logic: `onFrame()` schedules `RenderRequest` via `Handler.postDelayed`, capping at 30 FPS; `continueFrame()` for interactive mode.
- **Rationale**: Proven in original; balances smoothness and battery.
- **Alternative**: `Choreographer`. Rejected — original uses Handler; simpler to port.

## Risks / Trade-offs

- [GL context sharing between preview and wallpaper] → Use independent contexts; share only theme data.
- [Shader compilation failures on some GPUs] → Validate shader compilation and log errors; fallback to solid color.
- [Performance on low-end devices] → 30 FPS cap and on-demand rendering; disable noise texture on very low-end if needed.
- [Theme parsing edge cases] → Unit tests for parser; default values for missing params.
- [Memory leaks in GL resources] → Release textures/shaders in `onDestroy`.

## Migration Plan

1. Create `waves/` project with Gradle + Compose setup.
2. Copy assets from `clone_assets/` to `waves/app/src/main/assets/`.
3. Implement `ThemeParser` + `WaveTheme` + unit tests.
4. Implement `ShaderUtil` + `TextureUtil`.
5. Implement `WaveRenderer` (Kotlin GLES20).
6. Implement `WaveWallpaperService` + `WaveEngine`.
7. Implement Compose UI (`MainActivity`, `WallpaperPreview`, `ColorSelector`).
8. Integrate `WallpaperManager` apply flow.
9. Test on emulator + physical device.
10. Build release APK.

## Open Questions

- Should the preview use a separate `GLSurfaceView` or share the wallpaper's surface? (Deferred: start with separate for safety.)
- Should Material You dynamic colors be offered as an additional theme source? (Deferred: post-MVP.)
PY