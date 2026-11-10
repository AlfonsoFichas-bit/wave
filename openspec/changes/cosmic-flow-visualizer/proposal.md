# Proposal: Cosmic Flow 3D Visualizer & Compose App

## Why

Sony Xperia's "Cosmic Flow" is one of the most iconic interactive 3D live wallpapers and visualizers ever made for Android, featuring undulating cosmic energy ribbons deformed by harmonic wave turbulence and lookup noise across 3D perspective space.
The user wants to recreate Cosmic Flow 1:1 using the extracted assets and shaders from `Cosmic_Flow_S.apk`, implemented directly in Jetpack Compose so that it can initially run as an interactive standalone app (with full motion, touch response, and color themes) and subsequently be embedded as a real-time BPM-reactive background visualizer in a music player (Walkman).

## What Changes

- **Extract & Port Cosmic Flow 3D OpenGL ES Engine**: Port the reverse-engineered shaders (`flow_vertex.glsl`, `flow_fragment.glsl`, `background_vertex.glsl`, `background_fragment.glsl`) and texture assets (`flow_greyscale.png`, `noise.png`) into a pure Kotlin / OpenGL ES 2.0 renderer.
- **3D Perspective Mesh Generation**: Recreate the exact 3D grid and Model-View-Projection matrix (`uMVPMatrix`) used by Cosmic Flow to give the undulating surface its characteristic depth, tilt, and rotational perspective.
- **7 Classic Cosmic Flow Color Themes**: Implement the 7 original Sony color palettes extracted from `Cosmic_Flow_S.apk`:
  - **Amethyst** (purple/violet)
  - **Turquoise** (cyan/teal)
  - **Emerald** (green)
  - **Sapphire** (deep blue)
  - **Gold** (amber/gold)
  - **Silk** (platinum/white)
  - **Ruby** (crimson/red)
- **Jetpack Compose Visualizer Component**: Create a reusable, modular `@Composable` component (`CosmicFlowVisualizer`) that hosts the OpenGL renderer seamlessly in Compose via `AndroidView` (or `TextureView`/`GLSurfaceView`), with controls for speed, turbulence, and future audio/BPM hooks.
- **Standalone Interactive Showcase App**: A Material 3 Compose interface displaying the live 3D Cosmic Flow animation, interactive touch manipulation, and a color theme selector allowing instant switching between the 7 palettes.

## Capabilities

### New Capabilities
- `cosmic-flow-engine`: Pure Kotlin & OpenGL ES 2.0 3D renderer implementing the reverse-engineered Cosmic Flow vertex/fragment shaders, harmonic noise calculation, 3D grid mesh, and the 7 color presets.
- `cosmic-flow-compose-visualizer`: Reusable Jetpack Compose component and showcase UI hosting the Cosmic Flow engine, supporting theme selection, interactive gestures, and external audio/BPM modulation parameters.

### Modified Capabilities
<!-- None: This is a new visualizer system alongside the existing Wave live wallpaper. -->

## Impact

- **New Source Files**: Added to `waves` (or a dedicated submodule/package `com.wave.wallpaper.cosmic` or `com.buga.cosmicflow`).
- **Assets**: Integrates extracted shaders (`res/raw/*.glsl`) and textures (`flow_greyscale.png`, `noise.png`).
- **Dependencies**: Uses existing OpenGL ES 2.0 and Jetpack Compose BOM dependencies already configured in the project.
- **Reusability**: Designed to be copy-paste or module-ready for direct integration into the user's Walkman music player.
