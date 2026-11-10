# PROMPT PARA GOOGLE JULES — FASES 3 Y 4: TEMAS Y VISUALIZADOR JETPACK COMPOSE

Copia y pega el siguiente prompt directamente en **Google Jules**:

```markdown
# TASK: Implement Phase 3 and Phase 4 of Cosmic Flow (OpenSpec: cosmic-flow-visualizer)

## Context & Architecture
You are working on the Android project inside the `cosmic/` directory (`applicationId = "com.buga.cosmic"`, `namespace = "com.buga.cosmic"`).
Phases 1 and 2 implemented the OpenGL ES 2.0 shaders, mesh, and 3D renderer in package `com.buga.cosmic.gl`.

Now, you must implement **Phase 3** (Theme & Color Palette System) and **Phase 4** (Jetpack Compose Visualizer Component & Showcase UI) from `openspec/changes/cosmic-flow-visualizer/tasks.md`.

---

## PHASE 3: Theme & Color Palette System

### 3.1 Implement `CosmicTheme.kt`
Create `cosmic/app/src/main/java/com/buga/cosmic/theme/CosmicTheme.kt`:
- Data class / enum representing the theme presets:
  - `name: String`
  - `primaryColor: FloatArray` (RGB float values in [0.0f, 1.0f])
  - `secondaryColor: FloatArray` (RGB float values in [0.0f, 1.0f])
  - `displayName: String`
  - Optional thumbnail color `androidx.compose.ui.graphics.Color` for Compose chips
- Define the 7 classic Sony Xperia Cosmic Flow color presets:
  1. **AMETHYST** (Purple/Violet): Primary `[0.55f, 0.12f, 0.72f]`, Secondary `[0.85f, 0.25f, 0.95f]`
  2. **TURQUOISE** (Cyan/Teal): Primary `[0.05f, 0.45f, 0.58f]`, Secondary `[0.20f, 0.80f, 0.85f]`
  3. **EMERALD** (Green): Primary `[0.08f, 0.48f, 0.22f]`, Secondary `[0.30f, 0.85f, 0.40f]`
  4. **SAPPHIRE** (Blue): Primary `[0.08f, 0.25f, 0.68f]`, Secondary `[0.25f, 0.55f, 0.95f]`
  5. **GOLD** (Amber/Gold): Primary `[0.75f, 0.48f, 0.05f]`, Secondary `[0.98f, 0.80f, 0.20f]`
  6. **SILK** (Titanium/Rose): Primary `[0.52f, 0.45f, 0.48f]`, Secondary `[0.88f, 0.75f, 0.78f]`
  7. **RUBY** (Red/Crimson): Primary `[0.68f, 0.08f, 0.15f]`, Secondary `[0.95f, 0.35f, 0.25f]`
- Provide a `fun getAllThemes(): List<CosmicTheme>` helper and a `val DEFAULT = AMETHYST`.

### 3.2 Update `CosmicFlowRenderer.kt` for Dynamic Theme Uniforms
- Add a thread-safe method `setTheme(theme: CosmicTheme)` (or `setColors(primary: FloatArray, secondary: FloatArray)`) in `CosmicFlowRenderer.kt`.
- In `onDrawFrame`:
  - Pass `u_PrimaryColor` and `u_SecondaryColor` to the flow shader via `GLES20.glUniform3fv`.
  - Pass `u_PrimaryColor` to the background shader.
- Ensure theme switching happens dynamically and smoothly on the GL thread without reallocating or re-linking GL programs.

---

## PHASE 4: Jetpack Compose Visualizer & Showcase UI

### 4.1 Implement `CosmicFlowVisualizer.kt`
Create `cosmic/app/src/main/java/com/buga/cosmic/ui/CosmicFlowVisualizer.kt`:
- `@Composable fun CosmicFlowVisualizer`:
  - Parameters:
    - `modifier: Modifier = Modifier`
    - `theme: CosmicTheme = CosmicTheme.AMETHYST`
    - `bpm: Float = 120f` (controls animation time speed: multiplier `bpm / 120f`)
    - `amplitude: Float = 1.0f` (modulates wave turbulence/displacement)
    - `onSurfaceReady: ((CosmicFlowRenderer) -> Unit)? = null`
- Implementation details:
  - Use `AndroidView` to host a custom `GLSurfaceView`.
  - Configure `setEGLContextClientVersion(2)`.
  - Set `setZOrderMediaOverlay(false)` or default surface config so it blends properly in Compose.
  - Lifecycle management: Use `LocalLifecycleOwner.current` and a `DisposableEffect` with `LifecycleEventObserver` to call `glSurfaceView.onResume()` on `Lifecycle.Event.ON_RESUME` and `glSurfaceView.onPause()` on `Lifecycle.Event.ON_PAUSE`.
  - Gesture interaction: Attach a `pointerInput` on the Composable (or on the GLSurfaceView) to detect horizontal drag gestures and pass parallax `xOffset` to the renderer with smooth spring/easing interpolation.
  - Update blocks: When `theme`, `bpm`, or `amplitude` change in Compose recomposition, update the renderer's properties seamlessly.

### 4.2 Build Showcase UI (`MainActivity.kt`)
Update `cosmic/app/src/main/java/com/buga/cosmic/MainActivity.kt`:
- Create a modern Material 3 showcase screen:
  - **Full-screen background**: `CosmicFlowVisualizer` rendering in real time.
  - **Top Bar**: Minimalist glassmorphic / dark title ("Cosmic Flow 3D").
  - **Bottom Control Panel** (Card with elevated/semi-transparent background):
    1. **Theme Selector**: A horizontal scrollable row (`LazyRow`) of chips/buttons displaying all 7 presets (Amethyst, Turquoise, Emerald, Sapphire, Gold, Silk, Ruby). Tapping a chip immediately changes the visualizer theme.
    2. **BPM Slider**: Slider ranging from `60` to `180` BPM (default `120`). Moving the slider modulates the wave speed in real time to simulate audio tempo reactivity.
    3. **Amplitude Slider**: Slider ranging from `0.5` to `2.0` (default `1.0`) modulating turbulence intensity.
    4. **Drag hint**: Text reminding the user that horizontal swipes tilt the 3D ribbons in space.

---

## PHASE 5: Verification & Testing Criteria
1. Inside `cosmic/`, verify compilation by running:
   ```bash
   ./gradlew compileDebugKotlin
   ```
2. Verify that there are no unresolved references, syntax errors, or deprecation warnings breaking the build.
3. Update `openspec/changes/cosmic-flow-visualizer/tasks.md` marking Phase 3 and Phase 4 items as completed `[x]`.
```
