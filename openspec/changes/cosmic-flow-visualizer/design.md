# Design: Cosmic Flow 3D Engine & Jetpack Compose Visualizer

## Context

See `proposal.md` for background and user goals.
The codebase already features a functional OpenGL ES 2.0 pipeline and Jetpack Compose setup (`waves/`).
The extracted assets from `Cosmic_Flow_S.apk` provide the authentic GLSL shaders (`flow_vertex.glsl`, `flow_fragment.glsl`, `background_*.glsl`), noise textures (`noise.png`), and greyscale flow modulation textures (`flow_greyscale.png`).

## Goals / Non-Goals

**Goals:**
- Provide a pure Kotlin + OpenGL ES 2.0 implementation of Cosmic Flow running at solid 60/120 FPS on modern Android GPUs without native C++ libraries.
- Generate the precise 3D grid mesh, model rotation, and MVP matrix calculations found in the original Xperia engine.
- Implement the 7 classic color presets: Amethyst, Turquoise, Emerald, Sapphire, Gold, Silk, and Ruby.
- Package the visualizer into a standalone, modular Jetpack Compose component (`CosmicFlowVisualizer`) ready for future embed in a music player app.
- Provide real-time BPM and audio amplitude parameters that modulate the flow rate and wave turbulence.

**Non-Goals:**
- Direct integration into the music player app in this change (the user explicitly requested creating the standalone showcase app first, to be integrated into the music player later).
- Implementing Android WallpaperService for Cosmic Flow in this change (focused on the in-app Compose visualizer).

## Decisions

### 1. Engine Architecture: 3D Surface Grid vs Quad Strips
- **Decision**: Generate a 3D vertex grid (e.g. 32x32 or 48x48 mesh) transformed by `uMVPMatrix`, matching the `aPosition` attribute expected by `flow_vertex.glsl`.
- **Rationale**: The Cosmic Flow shader computes 3D vertex perturbations and surface normals in vertex space using `addNoise0(aPosition)` and `delta(...)`. A true 3D grid is required to match the authentic folding and flowing effect.
- **Alternatives Considered**: 2D ribbons (like Experience Flow). Rejected because Cosmic Flow's distinct look comes from its 3D perspective surface tilting into the screen.

### 2. Texture Optimization: Greyscale Flow Lookup
- **Decision**: Support the `flow_vertex_lookup.glsl` and `flow_fragment_lookup.glsl` with `flow_greyscale.png` as well as the analytical shader.
- **Rationale**: Using the pre-rendered noise lookup texture offloads heavy trigonometric operations on lower-end devices while preserving 100% visual fidelity.

### 3. Audio & BPM Modulation API
- **Decision**: Expose reactive parameters in `CosmicFlowVisualizer`:
  ```kotlin
  @Composable
  fun CosmicFlowVisualizer(
      theme: CosmicTheme = CosmicTheme.Amethyst,
      bpm: Float = 120f,
      amplitude: Float = 1.0f,
      modifier: Modifier = Modifier
  )
  ```
- **Rationale**: Internally, `bpm` scales the speed of `u_Time` dynamically, and `amplitude` scales `TURB` and `POWER_HI` parameters in the vertex shader, creating an organic pulsing effect that stays synchronized with song tempo.

## Risks / Trade-offs

- **[Risk] GLSurfaceView lifecycle within Jetpack Compose** → Handled via `DisposableEffect` with explicit `onResume()` / `onPause()` calls, preventing GPU context leaks when switching screens.
- **[Risk] Heavy fragment shader on high-resolution displays (QHD+)** → The background and flow shaders use low/medium precision (`precision lowp float;`) optimized by Sony for mobile GPUs.
