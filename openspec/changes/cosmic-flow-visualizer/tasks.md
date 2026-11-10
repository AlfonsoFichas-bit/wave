# Tasks: Cosmic Flow 3D Visualizer & Compose App

## 1. Asset & Shader Integration

- [x] 1.1 Copy extracted Cosmic Flow shaders and textures (`flow_vertex.glsl`, `flow_fragment.glsl`, `flow_vertex_lookup.glsl`, `flow_fragment_lookup.glsl`, `background_vertex.glsl`, `background_fragment.glsl`, `flow_greyscale.png`, `noise.png`) into the app assets directory and verify file availability.
- [x] 1.2 Implement `CosmicShaderUtil` to load, compile, and link the background and flow programs, verifying that shader compilation logs report no errors.

## 2. 3D Grid & Perspective Engine

- [x] 2.1 Implement `CosmicMesh` generating the 3D grid vertex buffer and index buffer, verifying buffer sizes and vertex coordinate ranges.
- [x] 2.2 Implement `CosmicMatrixState` computing the 3D Model-View-Projection matrix (`uMVPMatrix`) with camera perspective, pitch tilt, and rotation.
- [x] 2.3 Implement `CosmicFlowRenderer` executing the background gradient pass and the deformed 3D cosmic mesh pass, verifying that frames render without OpenGL errors.

## 3. Theme & Color Palette System

- [x] 3.1 Implement `CosmicTheme` data class and define the 7 classic presets: Amethyst, Turquoise, Emerald, Sapphire, Gold, Silk, and Ruby.
- [x] 3.2 Wire theme uniforms into `CosmicFlowRenderer` so switching themes dynamically alters background colors and flow tint without recreating GL programs.

## 4. Jetpack Compose Visualizer & Showcase UI

- [x] 4.1 Create `@Composable fun CosmicFlowVisualizer` wrapping the OpenGL renderer inside `AndroidView` with proper lifecycle management (`onResume`/`onPause`).
- [x] 4.2 Add `bpm` (tempo speed factor) and `amplitude` (turbulence intensity) parameters to `CosmicFlowVisualizer` for future music player synchronization.
- [x] 4.3 Build a Material 3 showcase screen displaying the live 3D Cosmic Flow visualizer, gesture drag/tilt controls, and a palette selector.

## 5. Verification & Testing

- [ ] 5.1 Write unit tests for `CosmicTheme` presets and mesh generation buffers, verifying test passes via `./gradlew testDebugUnitTest`.
- [ ] 5.2 Assemble and install the debug build on the connected device (`./gradlew installDebug`), verifying smooth 60/120 FPS animation and theme switching.
