# Análisis profundo de `wallpaper_wave.apk` (Sony "Experience Flow") y plan de clon "wave"

> Fuente: `C:\Users\alfon\TGZ\wave\wallpaper_wave.apk`  
> SHA-256: `912c78ef951fe804f536eedacf11f56cfb55852ebebd6ea63e71ee761e603c0e`  
> Tamaño: 7,361,050 bytes  
> Análisis realizado con REA (inventario/extract, firma APK, exports nativos) + Android SDK (aapt, dexdump, strings) sobre los artefactos extraídos en `C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9`.

---

## 1. Identidad y metadatos

| Campo | Valor |
|---|---|
| Package | `com.sonymobile.experienceflow2` |
| Version name | `3.2.A.0.3` |
| Version code | `0x640003` (412675?) |
| minSdkVersion | 23 (Android 6.0) |
| targetSdkVersion | 24 (Android 7.0) |
| sharedUserId | `com.sonymobile.experienceflow2` |
| OpenGL ES | 2.0 requerido (`glEsVersion=0x20000`, required) |
| allowBackup | false |
| supportsRtl | true |

### Permisos declarados
- `com.sonymobile.runtimeskinning.permission.RECEIVE_SKIN_CHANGED`
- `com.sonymobile.experienceflow2.permission.INJECT_DEPENDENCIES` (signature, protectionLevel 2)
- `android.permission.INTERNET`
- `android.permission.ACCESS_NETWORK_STATE`
- `android.permission.BIND_WALLPAPER` (para el servicio de wallpaper)
- `android.permission.MASTER_CLEAR` (para MasterResetReceiver)

### Componentes
- **Application**: `com.sonymobile.experienceflow.wallpaper.ExperienceFlowApplication`
- **Service (Wallpaper)**: `ExperienceFlowService` (exported via intent `android.service.wallpaper.WallpaperService`, permission BIND_WALLPAPER), metadata `android.service.wallpaper` -> `@xml/wallpaper`
- **Activity (Settings)**: `ExperienceFlowPreferencesActivity` (exported, configChanges 0x480)
- **Receiver**: `ThemeChangeReceiver` (permission SEND_SKIN_CHANGED, action SKIN_CHANGED)
- **Service**: `ThemeChangeReceiver$ApplyAccentColorService`
- **Receiver**: `MasterResetReceiver` (permission MASTER_CLEAR, action MASTERRESET, category DEFAULT)
- **Service**: `MasterResetReceiver$ResetPreferencesService`

### Firma
- Solo scheme v1 (JAR signing). Certificado SHA-256: `6339375ac295cb0cd22811b97accd40104bd4a0185d4dd2289b81860c15d623c`. No v2/v3/v3.1. (No se verificó criptográficamente.)

---

## 2. Estructura del APK (83 nodos / 91 ocurrencias)

```
wallpaper_wave.apk
├── AndroidManifest.xml
├── classes.dex                      (76 clases, 83,932 bytes)
├── lib/armeabi-v7a/
│   ├── libgnustl_shared.so          (661,132 bytes, runtime C++)
│   └── liblwplocal.so               (95,512 bytes, renderer OpenGL ES 2.0)
├── assets/
│   ├── gfxbackground.frag / .vert + .header
│   ├── gfxline.vert / .frag + .header (A, B)
│   ├── footer.frag.base / .vert.base
│   ├── header.frag.base / .vert.base
│   ├── inputs.base
│   ├── LICENSE.html
│   ├── noise.png                    (2,542,704 bytes, textura de ruido)
│   ├── lwp.-1.theme                 (transitions + 6 variantes .0..? )
│   ├── lwp.-2.theme                 (default / blanco)
│   ├── lwp.0.theme .. lwp.15.theme  (16 temas de color)
│   └── (shaders base)
├── res/
│   ├── drawable-{hdpi,xhdpi,xxhdpi}-v4/
│   │   ├── *_preview.png (16 temas + ic_launcher/ic_wallpaper)
│   ├── layout/settings_item.xml
│   ├── raw/defaultcontainer_binary  (Google Analytics config, UA-51142570-8)
│   └── xml/wallpaper.xml
├── resources.arsc
└── META-INF/ (MANIFEST.MF, SONY-ERICSSON-E-PLATFORM-SIGNING-*.RSA/.SF)
```

---

## 3. Native library `liblwplocal.so` (renderer)

### 3.1 Clases C++ expuestas (mangled)
- `renderer` (init, render, getfile, createVertices, createIndices, buildShaderFile)
- `Curve` (constructor, updateRenderer, requestColors, updateScreenLayout, touch/touchDown/touchUp, activate/deactivate, getSleepRecommendation)
- `program` (bind, update, shader, createProgram, getattrib, getuniform, uniforms, attribs)
- `texture` (set, bind)
- `buffer` / `bufferconfig` / `varray` (gestión de VBOs)
- `uniform` (set float, touched)
- `fsshader` (run)
- `CurveClient` (cliente JNI)

### 3.2 JNI methods (Java_com_sonymobile_experienceflow_renderer_CurveRenderer_*)
```
init(AAssetManager)
render()
requestColors(AAssetManager, int)
updateScreenLayout(int,int,int,int,int,int)
touchDown(float,float)
touch(float,float)
touchUp(float,float)
activate()
deactivate()
getSleepRecommendation() -> int/boolean
```

### 3.3 Parámetros de tema (theme parameters)
El renderer parsea archivos `.theme` con `readTheme<istringstream>` -> `std::map<std::string, themeparameter>`.

Claves observadas en los `.theme`:
- `clearColor` (RGBA)
- `bgc1..bgc4` (colores de fondo, RGBA)
- `lc1..lc6` (colores de líneas, RGBA)
- `curveAmplitudeRange` (min,max,0,0)
- `curveFrequencyRange`
- `curveWidthRange`
- `curveSpeedRange`
- `curveOffsetRange`
- `curveBendRange`
- `curveOffsetSpeed`
- `curveBendFactor`
- `curveBendSpeed`
- `lineCount`
- `noiseTextureScale`
- `noise` (nombre de textura)
- `time`
- `lsByx`
- `precalc_ratio`, `precalc_radlc`, `precalc_randval`, `precalc_car`, `precalc_curveParams`
- `linedata`
- `header.` / `footer.` (shaders base)
- `.transitions` (número de variantes en lwp.-1.theme)
- variantes con prefijo `.<n>.` (ej. `.0.bgc4 ...`) para transiciones de color entre temas

### 3.4 Shaders (assets)
- `gfxbackground.vert` / `.frag` + `.header` (fondo animado)
- `gfxline.vert` / `gfxlineA.frag` / `gfxlineB.frag` + headers (curvas/líneas)
- `footer.frag.base` / `footer.vert.base` (footer overlay)
- `header.frag.base` / `header.vert.base` (header overlay)
- `inputs.base`

### 3.5 Texturas
- `noise.png` (2.5 MB) usada como textura de ruido (`noiseTextureScale`).
- Previews PNG por tema (ic_launcher, ic_wallpaper, *_preview).

### 3.6 Strings nativos relevantes
- `"remove noise texture"`, `"noise texture: %d, %d, %p"`
- `"remove bg texture"`, `"bg texture: %d, %d, %p"`
- `"Could not decode %s, file missing or corrupt!"`
- `"Requesting color: "`
- `".theme"`, `"noise.png"`, `".transitions"`
- GCC 4.6/4.8, gold 1.11 (compilado con NDK antiguo)

---

## 4. Lógica Java (DEX) — clases clave

### 4.1 `ExperienceFlowService` (WallpaperService)
- `onCreate()` -> super
- `onCreateEngine()` -> crea `ExperienceFlowService$1` (engine)
- `onDestroy()` -> super + `System.exit(0)` (mata el proceso)

### 4.2 `ExperienceFlowService$1` (Engine)
- Campos: `mView` (ExperienceFlowGLSurfaceView), `this$0`
- `createGLSurfaceView()` -> crea la view con el renderer
- `onCommand(...)` maneja comandos del lockscreen:
  - `com.sonymobile.lockscreen.wallpaper.command.POINTER_DOWN` -> `renderer.touchDown(x,y)`
  - `POINTER_MOVE` -> `renderer.touch(x,y)`
  - `POINTER_UP` -> `renderer.touchUp(x,y)`
  - `ACCELERATE` (si |v5| <= 10000) -> `view.accelerate(v5)`
  - default -> super.onCommand

### 4.3 `ExperienceFlowRenderer` (GLSurfaceView.Renderer)
Campos: `mContext`, `mSurfaceView`, `mWidth`, `mHeight`, `mFramerateController`, `curveRenderer` (CurveRendererImpl), `location[]`, `ax`, `ay`, `doReset`.

Métodos:
- Constructor `(Context, GLSurfaceView)`
- `onSurfaceCreated(gl, config)` -> init GL, curveRenderer
- `onSurfaceChanged(gl, w, h)` -> updateScreenLayout
- `onDrawFrame(gl)` -> render loop (poke, pushTouchUp, curveRenderer.render)
- `touchDown(x,y)`, `touch(x,y)`, `touchUp(x,y)`
- `handleSwipe(SwipeData)`
- `accelerate(float)`
- `updateColor(SharedPreferences)`
- `poke()`, `pushTouchUp()`
- `getCapabilities()`, `getFramerateController()`
- `access$000` (updateColor desde SharedPreferences listener)
- `access$100` (getContext)

### 4.4 `CurveRenderer` / `CurveRendererImpl`
- `init(AAssetManager)`
- `render()`
- `requestColors(AAssetManager, int)` (carga tema por índice)
- `updateScreenLayout(int,int,int,int,int,int)`
- `touch/touchDown/touchUp (float,float)`
- `activate()` / `deactivate()`
- `getSleepRecommendation()`
- `pleaseRender()` (callback renderListener)
- `setOnRenderListener(...)`

### 4.5 `ColorPicker`
- Grupos de colores: `importSemcColorGroup1..4`, `importVerizonRedColorGroup`, `importAllSemcColors`, `importSemcColors`
- `getAllColors()`, `getSemcColors()`, `sAllColors`, `sColors`
- `getCurrentColorIndex()`, `getColorIndexFromPreferences()`, `getDefaultLiveWallpaperIndex()`
- `registerPreferencesListener()`, `destroy()`
- `mCurrentColorIndex`, `mPreferences`, `mContext`

### 4.6 `FramerateControllerBase` / `FramerateController` / `ProxyFramerateController`
- Control de tasa de frames (render on demand / continuo).

### 4.7 `Capabilities`
- Capacidades del dispositivo (probablemente para elegir calidad).

### 4.8 `RuntimeSkinning` (Sony)
- `RuntimeSkinningHelper`, `RuntimeSkinningUtil`, `SkinGlue` (V16/V17/V18), `ReflectionUtils`
- Permite aplicar "skins" (temas del sistema Sony) dinámicamente: cambia colores accent.

### 4.9 `ThemeChangeReceiver`
- Recibe `SKIN_CHANGED` (permission SEND_SKIN_CHANGED) -> `ApplyAccentColorService` aplica color accent.

### 4.10 `MasterResetReceiver`
- Recibe `MASTERRESET` -> `ResetPreferencesService` resetea preferencias.

### 4.11 `ExperienceFlowPreferencesActivity`
- Settings UI con licencia (LoadLicenseTask), color picker, etc.

### 4.12 `ExperienceFlowApplication`
- Application base.

### 4.13 `SwipeData`, `ColorInfo`, `Refreshable`
- Modelos de datos.

---

## 5. Pseudocódigo — Flujo principal

### 5.1 Inicio del wallpaper
```
function ExperienceFlowService.onCreate():
    super.onCreate()   // GLWallpaperService

function ExperienceFlowService.onCreateEngine():
    engine = new ExperienceFlowService$1(this)
    return engine

// Engine (ExperienceFlowService$1)
function Engine.createGLSurfaceView():
    view = new ExperienceFlowGLSurfaceView(this, context)
    // view constructor:
    //   super(GLSurfaceView) con EGL context client version 2
    //   setEGLConfigChooser(8,8,8,8,0,0)  // RGBA8
    //   holder.setFormat(TRANSLUCENT)
    //   setPreserveEGLContextOnPause(true)
    //   renderer = new ExperienceFlowRenderer(context, view)
    //   setRenderer(renderer); setRenderMode(RENDERMODE_CONTINUOUSLY o WHEN_DIRTY)
    return view

function Engine.onCommand(action, x, y, z, extras, resultRequested):
    if action == "POINTER_DOWN":
        renderer.touchDown(x,y)
    elif action == "POINTER_MOVE":
        renderer.touch(x,y)
    elif action == "POINTER_UP":
        renderer.touchUp(x,y)
    elif action == "ACCELERATE" and abs(x) <= 10000:
        view.accelerate(x)
    return super.onCommand(...)
```

### 5.2 Render loop (OpenGL ES 2.0)
```
function ExperienceFlowRenderer.onSurfaceCreated(gl, config):
    // init GL state
    glClearColor(0,0,0,0)
    glEnable(GL_BLEND)  // probable
    curveRenderer = new CurveRendererImpl()
    curveRenderer.init(assetManager)   // carga shaders, noise, default theme

function ExperienceFlowRenderer.onSurfaceChanged(gl, w, h):
    mWidth = w; mHeight = h
    curveRenderer.updateScreenLayout(0,0,w,h, w,h)  // o con insets

function ExperienceFlowRenderer.onDrawFrame(gl):
    glClear(GL_COLOR_BUFFER_BIT)
    // framerate control
    if framerateController.shouldRender():
        curveRenderer.render()   // dibuja fondo + curvas
    // touch handling
    pushTouchUp()  // si hubo touchUp pendiente
    poke()         // notifica framerate controller
```

### 5.3 Render del renderer nativo (CurveRendererImpl.render -> JNI Curve.render)
```
// nativo (pseudocódigo reconstruido de exports + strings)
function Curve.render():
    // 1. actualizar tiempo
    time += delta
    // 2. fondo (gfxbackground shader)
    bind program "gfxbackground"
    set uniforms: time, noiseTextureScale, clearColor, bgc1..bgc4
    bind noise texture
    draw fullscreen quad (createVertices/createIndices)
    // 3. curvas (gfxline shader)
    for i in 0..lineCount-1:
        bind program "gfxline"
        compute curve params from precalc_* + curve*Range
        set uniforms: amplitude, frequency, width, speed, offset, bend, ...
        set line color lc[i]
        draw line strip
    // 4. header/footer overlays (opcional)
    // 5. framerate: pleaseRender -> request render if needed
```

### 5.4 Carga de tema (requestColors)
```
function CurveRenderer.requestColors(assetManager, themeIndex):
    path = "lwp." + themeIndex + ".theme"   // o lwp.-1.theme para transitions
    // nativo: theme(assetManager, index) -> readTheme
    map = readTheme(file "lwp.<index>.theme")
    // parsea pares key value...
    clearColor = map["clearColor"]
    for i in 1..4: bgc[i] = map["bgc"+i]
    for i in 1..6: lc[i] = map["lc"+i]
    curveAmplitudeRange = map["curveAmplitudeRange"]
    ... (todos los rangos)
    lineCount = map["lineCount"]
    noiseTextureScale = map["noiseTextureScale"]
    // si .transitions: carga variantes .0, .1, ... para transición de color
```

### 5.5 Touch / interacción
```
function ExperienceFlowRenderer.touchDown(x, y):
    curveRenderer.touchDown(x, y)
    // nativo: guarda punto de toque, activa efecto de ondas

function ExperienceFlowRenderer.touch(x, y):
    curveRenderer.touch(x, y)

function ExperienceFlowRenderer.touchUp(x, y):
    curveRenderer.touchUp(x, y)
    pushTouchUp()  // deferred

function Curve.touchDown(x,y):
    // convierte a coords GL, crea/activa curva de interacción
```

### 5.6 Aceleración (lockscreen swipe)
```
function view.accelerate(v):
    renderer.accelerate(v)  // nativo/Java ajusta velocidad de curvas
```

### 5.7 Color / theming
```
function ExperienceFlowRenderer.updateColor(prefs):
    colorIndex = ColorPicker.getColorIndexFromPreferences(prefs)
    curveRenderer.requestColors(assetManager, colorIndex)
    // si RuntimeSkinning disponible, aplicar accent color del sistema
```

### 5.8 Framerate
```
function FramerateController:
    // decide si renderizar continuo o on-demand (sleep recommendation)
    shouldRender() -> boolean
    getSleepRecommendation() -> usado por el sistema para dormir el wallpaper
```

---

## 6. Pseudocódigo — Clon "wave" (implementación sugerida)

### 6.1 Arquitectura del clon
- Package: `com.wave.wallpaper` (o similar)
- Min SDK 21+ (para compatibilidad amplia), target SDK actual.
- OpenGL ES 2.0 (o 3.0 opcional).
- WallpaperService + Engine + GLSurfaceView + Renderer (patrón del original).
- Temas como archivos `.theme` en `assets/` + parser propio.
- Color picker con paleta propia (no SEMC).

### 6.2 Componentes mínimos
```
WaveWallpaperService extends GLWallpaperService
  onCreateEngine() -> WaveEngine
WaveEngine
  createGLSurfaceView() -> WaveGLSurfaceView (EGL RGBA8, translucent)
  onCommand() -> touch/accelerate (opcional)
WaveRenderer implements GLSurfaceView.Renderer
  onSurfaceCreated/Changed/DrawFrame
  touchDown/touch/touchUp
  requestTheme(index)
  updateColor(prefs)
WaveNativeRenderer (C++ via JNI) o equivalente en Kotlin/Java con GLES20
  // Opción A: portar liblwplocal.so (recompilar para arm64/x86)
  // Opción B: reescribir en Kotlin usando GLES20 + shaders (más portable)
```

### 6.3 Parser de tema (Kotlin)
```
data class Theme(
  clearColor: FloatArray(4),
  bgc: List<FloatArray>,   // 4
  lc: List<FloatArray>,    // 6
  curveAmplitudeRange: FloatArray(4),
  curveFrequencyRange: FloatArray(4),
  curveWidthRange: FloatArray(4),
  curveSpeedRange: FloatArray(4),
  curveOffsetRange: FloatArray(4),
  curveBendRange: FloatArray(4),
  curveOffsetSpeed: Float,
  curveBendFactor: Float,
  curveBendSpeed: Float,
  lineCount: Float,
  noiseTextureScale: Float,
  transitions: Int = 0
)

fun parseTheme(text: String): Theme {
  val map = mutableMapOf<String, FloatArray>()
  val tokens = text.split(Regex("\\s+"))
  var i = 0
  while (i < tokens.size) {
    val key = tokens[i]; i++
    // leer 4 floats si existen
    val vals = FloatArray(4) { if (i < tokens.size && tokens[i].toFloatOrNull()!=null) tokens[i++].toFloat() else 0f }
    map[key] = vals
  }
  // manejar claves especiales: .transitions, .<n>.bgc...
  return Theme(...)
}
```

### 6.4 Render (Kotlin/GLES20) — esqueleto
```
class WaveRenderer(ctx, view) : GLSurfaceView.Renderer {
  var width=0; height=0
  var programBg=0; programLine=0
  var noiseTex=0
  var theme = loadTheme(0)
  var time=0f

  override fun onSurfaceCreated(gl, config) {
    GLES20.glClearColor(0f,0f,0f,0f)
    GLES20.glEnable(GLES20.GL_BLEND)
    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    programBg = buildProgram("gfxbackground.vert", "gfxbackground.frag")
    programLine = buildProgram("gfxline.vert", "gfxlineA.frag")
    noiseTex = loadTexture("noise.png")
    theme = parseTheme(readAsset("lwp.0.theme"))
  }

  override fun onSurfaceChanged(gl, w, h) {
    width=w; height=h
    GLES20.glViewport(0,0,w,h)
  }

  override fun onDrawFrame(gl) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    time += 0.016f
    // fondo
    GLES20.glUseProgram(programBg)
    setUniform(programBg, "u_time", time)
    setUniform(programBg, "u_noiseScale", theme.noiseTextureScale)
    setUniform(programBg, "u_clearColor", theme.clearColor)
    for i in 0..3: setUniform(programBg, "u_bgc${i+1}", theme.bgc[i])
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, noiseTex)
    drawFullscreenQuad()
    // curvas
    GLES20.glUseProgram(programLine)
    for i in 0 until theme.lineCount.toInt() {
      val amp = lerp(theme.curveAmplitudeRange[0], theme.curveAmplitudeRange[1], rand(i))
      ... // calcular params
      setUniform(programLine, "u_color", theme.lc[i % theme.lc.size])
      drawLineStrip(curvePoints(i))
    }
  }

  fun touchDown(x,y) { /* activar efecto */ }
  fun touch(x,y) { /* actualizar */ }
  fun touchUp(x,y) { /* desactivar */ }
}
```

### 6.5 Shaders portados
- Copiar `gfxbackground.vert/frag`, `gfxline.vert`, `gfxlineA.frag`, `gfxlineB.frag`, `header.*`, `footer.*` a `assets/shaders/`.
- Ajustar paths en buildProgram (leer desde assets).

### 6.6 Temas
- Copiar `lwp.0.theme`..`lwp.15.theme`, `lwp.-1.theme`, `lwp.-2.theme` a `assets/themes/`.
- Renombrar a `theme_0.theme` etc. para claridad.

### 6.7 Color picker
- Reemplazar grupos SEMC por paleta propia (ej. 16 colores).
- Persistir índice en SharedPreferences.

### 6.8 Settings Activity
- Preferencias: selección de tema, velocidad, densidad de líneas, etc.
- Licencia opcional (omitir o propia).

### 6.9 Runtime skinning (opcional)
- Omitir en clon (Sony-specific). O implementar DynamicColors (Material You) para Android 12+.

---

## 7. Hallazgos adicionales

- **Analytics**: `defaultcontainer_binary` contiene config de Google Analytics con tracking ID `UA-51142570-8` y parámetros de screen/event tracking. El clon no debe incluir este tracking (o usar propio).
- **Noise texture**: 2.5 MB PNG, se usa como textura de ruido en el shader. Se puede regenerar proceduralmente o comprimir.
- **Previews**: 16 temas × 3 densidades = 48 PNGs de preview.
- **Transiciones**: `lwp.-1.theme` define 6 variantes (`.0`..`.5`) con colores interpolados para transición suave entre temas.
- **Lockscreen integration**: comandos `com.sonymobile.lockscreen.wallpaper.command.*` (Sony-specific). Omitir en clon o adaptar a estándar.
- **Master reset**: resetea preferencias al factory reset. Opcional.

---

## 8. Riesgos / desconocidos

- El renderer nativo usa STL (gnustl) y fue compilado para armeabi-v7a. Para clon moderno, recompilar para arm64-v8a y x86_64 o portar a Kotlin.
- `getSleepRecommendation` y FramerateController interactúan con el sistema de wallpaper (doze). En clon, usar `WallpaperService.Engine.onVisibilityChanged` y `setTouchEventsEnabled`.
- `RuntimeSkinning` es Sony-specific; no portable.
- No se decompiló el DEX a Java (JADX no disponible); el pseudocódigo se infiere de bytecode + strings + native exports. Para fidelidad exacta, usar jadx sobre classes.dex.

---

## 9. Checklist de implementación del clon "wave"

- [ ] Crear proyecto Android (Kotlin, minSdk 21, targetSdk 34).
- [ ] Implementar `WaveWallpaperService` + Engine + GLSurfaceView.
- [ ] Portar shaders a `assets/shaders/`.
- [ ] Portar temas a `assets/themes/`.
- [ ] Implementar parser de `.theme`.
- [ ] Implementar renderer en Kotlin (GLES20) replicando fondo + curvas + ruido.
- [ ] Implementar touch (touchDown/touch/touchUp) y aceleración.
- [ ] Implementar color picker + SharedPreferences.
- [ ] Implementar settings Activity.
- [ ] Generar previews e iconos.
- [ ] (Opcional) Portar native lib a arm64 para performance.
- [ ] Probar en emulador/dispositivo.
- [ ] Eliminar analytics de Sony / tracking.

---

## 10. Código decompilado real

El código Java completo decompilado (jadx 1.5.0) está en `CODIGO_DECOMPILADO_WAVE.md` y en `reference_java/`. Incluye `GLWallpaperService`, `ExperienceFlowService`, `ExperienceFlowRenderer`, `CurveRenderer`/`CurveRendererImpl`, `ColorPicker`/`ColorInfo`, `FramerateControllerBase`, constantes y pseudocódigo exacto del clon.

---

*Documento generado a partir de ingeniería inversa estática. No se ejecutó el APK ni se verificó su firma.*