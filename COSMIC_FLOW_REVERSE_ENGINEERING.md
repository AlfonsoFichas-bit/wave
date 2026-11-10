# 🔍 Informe de Ingeniería Inversa: Cosmic_Flow_S.apk

**Archivo analizado:** `Cosmic_Flow_S.apk` (3.4 MB, SHA256: `a53e1dacdf6e2e5270d69f6569a4e6899d78d886d472830500cda0c4b20cf616`)
**Tipo:** Android Live Wallpaper (Papel Tapiz Animado)
**Package:** `com.sonyericsson.cosmicflow`
**Versión de Android SDK:** AppCompat + AndroidX (legacy support library)
**OpenGL:** ES 2.0

---

## 1. Arquitectura General

La aplicación es un **Live Wallpaper** (servicio de papel tapiz animado) de Sony Ericsson que renderiza un efecto de "flujo cósmico" en tiempo real usando OpenGL ES 2.0. No es una Activity tradicional; se ejecuta como fondo animado de pantalla principal.

### Estructura de clases principales (`com.sonyericsson.cosmicflow`):

| Clase | Responsabilidad |
|-------|-----------------|
| `LiveWallpaper` | Servicio de wallpaper, punto de entrada del sistema Android |
| `LiveWallpaper$WallpaperEngine` | Motor del wallpaper, gestiona ciclo de vida (visible/hidden, touch) |
| `LiveWallpaper$WallpaperGLSurfaceView` | Vista GLSurfaceView para renderizado OpenGL |
| `LiveWallpaper$WallpaperEGLConfigChooser` | Configuración EGL para contexto OpenGL |
| `FlowRenderer` | Renderizador principal OpenGL ES 2.0 (onDrawFrame, onSurfaceChanged, etc.) |
| `FlowMeshPrimitive` | Primitiva de malla para el flujo ondulante |
| `SquareMeshPrimitive` | Primitiva de malla cuadrada |
| `MeshPrimitive` | Clase base de primitivas de malla |
| `ShaderFactory` | Fábrica de shaders GLSL |
| `ShaderProgram` | Programa de shader (vertex + fragment) |
| `VertexBufferObject` | VBO para geometría de malla |
| `Config` | Configuración de OpenGL/renderizado |
| `Texture` | Gestión de texturas OpenGL |
| `Settings` | Configuración de preferencias del usuario |
| `ColorPicker` | Selector de colores primario/secundario |
| `ColorInfo` | Modelo de datos de color |
| `Customization` | Integración con Sony Customization provider |
| `MasterResetReceiver` | Receptor de reseteo maestro (borrar preferencias) |
| `ThemeChangeReceiver` | Receptor de cambio de tema del sistema |
| `BuildConfig` | Configuración de build |
| `C0001R` / `R` | Clases de recursos generadas |

---

## 2. Funcionalidad Detallada

### 2.1 Renderizado OpenGL ES 2.0 (`FlowRenderer`)

**Métodos clave:**
- `onSurfaceCreated()` - Inicializa OpenGL, carga shaders, crea texturas
- `onSurfaceChanged()` - Configura viewport y matriz de proyección
- `onDrawFrame()` - Bucle principal de renderizado (llamado cada frame)
- `matrixSetUp()` - Configura matriz MVP (Model-View-Projection)
- `renderFlow()` - Renderiza el flujo ondulante principal
- `renderBackground()` - Renderiza el fondo estático/con textura

**Variables de instancia:**
- `mFlowVertexShader` / `mFlowFragmentShader` - Código fuente de shaders GLSL
- `mFlowProgram` - Programa de shader compilado
- `mFlowTex` - ID de textura OpenGL
- `mFlowBitmap` - Bitmap de origen para la textura
- `mFlowVertexBufferObject` - VBO con geometría de la malla
- `newFlowPrimitive` - Instancia de la primitiva de malla actual

**Malla de flujo (`FLOW_MESH_SIZE`):**
- Una malla de vértices que se deforma con ruido procedural
- Cada frame, los vértices se desplazan usando ondas sinusoidales superpuestas
- La transparencia se calcula basada en la normal de la superficie (área del grid transformado)

### 2.2 Shaders GLSL

Se identificaron 6 archivos de shader en `res/raw/`:

| Archivo | Estado | Descripción |
|---------|--------|-------------|
| `flow_vertex.glsl` | Activo | Vertex shader del flujo con ruido procedural por ondas sinusoidales |
| `flow_fragment.glsl` | Activo | Fragment shader del flujo con color primario/secundario y alpha por normal |
| `flow_vertex_lookup.glsl` | Obsoleto | Vertex shader antiguo (usa textura de ruido lookup) |
| `flow_fragment_lookup.glsl` | Obsoleto | Fragment shader antiguo |
| `background_vertex.glsl` | Activo | Vertex shader del fondo con ondulación vertical |
| `background_fragment.glsl` | Activo | Fragment shader del fondo (aplica color sobre textura) |

**Parámetros uniformes del shader de flujo:**
- `u_Time` - Tiempo transcurrido (para animación)
- `u_NoiseScale` - Escala del ruido
- `u_PrimaryColor` - Color primario (vec3)
- `u_SecondaryColor` - Color secundario (vec3)
- `u_NoisePos01` - Posiciones de ruido (vec4)
- `uMVPMatrix` - Matriz modelo-vista-proyección
- `sTexture` - Sampler de textura

**Constantes del vertex shader de flujo:**
- `POWER_LO = 0.03`, `POWER_HI = 0.05` - Potencia de ruido bajo/alto
- `TURB = 3.5` - Turbulencia
- `SPEED = 0.2` - Velocidad de animación
- `NOISE_SCALE = vec4(0.7, 1.3, 0.7, 1.0)` - Escala de ruido por eje

### 2.3 Sistema de Colores

- **Color primario y secundario** configurables por el usuario
- `ACCENT_COLOR_RES_NAME` / `ACCENT_COLOR_RES_TYPE` - Referencia a color de acento del tema
- `ThemeChangeReceiver` + `ApplyAccentColorService` - Aplica color de acento cuando cambia el tema del sistema
- `ColorPicker` - UI para seleccionar colores
- `ColorInfo` - Modelo con datos de color

### 2.4 Preferencias y Configuración (`Settings`)

- `cosmicFlowPrefs` - Nombre del archivo SharedPreferences
- `Settings$1` - Listener de cambios en preferencias
- `Settings$C00052` - Clase anónima/interna

### 2.5 Integración con Sony Ericsson

| Componente | Descripción |
|------------|-------------|
| `com.sonyericsson.airtouch.service` | Servicio AirTouch (gesto de aire para interactuar) |
| `ACTION_ENABLE_AIRTOUCH` / `ACTION_DISABLE_AIRTOUCH` | Acciones para habilitar/deshabilitar AirTouch |
| `ACTION_MASTERRESET` | Acción de reseteo maestro |
| `content://com.sonyericsson.provider.customization/settings` | Proveedor de customización de Sony |
| `com.sonyericsson.settings.MASTERRESET` | Acción de reseteo de configuración Sony |
| `MasterResetReceiver` | Recibe reseteo y borra preferencias (`ResetPreferencesService`) |

---

## 3. Recursos (Assets)

### 3.1 Imágenes de Gemas (Drawables)

Estas imágenes se usan como texturas/acento en el wallpaper:

| Archivo | Tamaño (base) | Descripción |
|---------|--------------|-------------|
| `amber.png` | mdpi: 4,334 bytes | Gamba ámbar |
| `amethyst.png` | mdpi: 4,126 bytes | Gamba amatista |
| `emerald.png` | mdpi: 4,396 bytes | Gamba esmeralda |
| `gold.png` | mdpi: 4,150 bytes | Gamba oro |
| `ruby.png` | mdpi: 4,332 bytes | Gamba rubí |
| `sapphire.png` | mdpi: 4,263 bytes | Gamba zafiro |
| `silk.png` | mdpi: 4,192 bytes | Gamba seda |
| `turquoise.png` | mdpi: 4,278 bytes | Gamba turquesa |
| `bg_grey.png` | 175,302 bytes | Fondo gris (textura grande) |
| `ic_launcher.png` | Varios DPI | Icono de la app |
| `launcher.png` | Varios DPI | Icono launcher |
| `frame.9.png` | 412 bytes | Frame 9-patch |

Disponibles en múltiples densidades: mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi.

### 3.2 Shaders GLSL (res/raw)

| Archivo | Tamaño | Uso |
|---------|--------|-----|
| `flow_vertex.glsl` | 111 líneas | Vertex shader del flujo |
| `flow_fragment.glsl` | 32 líneas | Fragment shader del flujo |
| `background_vertex.glsl` | 39 líneas | Vertex shader del fondo |
| `background_fragment.glsl` | 24 líneas | Fragment shader del fondo |
| `flow_vertex_lookup.glsl` | 100 líneas | Vertex shader lookup (obsoleto) |
| `flow_fragment_lookup.glsl` | 45 líneas | Fragment shader lookup (obsoleto) |

### 3.3 Texturas de Ruido (res/raw)

| Archivo | Tamaño | Uso |
|---------|--------|-----|
| `noise.png` | 1,069,133 bytes (~1 MB) | Textura de ruido para el flujo |
| `flow_greyscale.png` | 42,631 bytes | Textura en escala de grises para peso de color |

### 3.4 XML de Recursos (res/xml)

| Archivo | Contenido (binario, necesita decodificar) |
|---------|------------------------------------------|
| `cosmicflow.xml` | Configuración del wallpaper (metadata, preview, etc.) |
| `default_settings.xml` | Valores por defecto de preferencias |

### 3.5 Layouts (res/layout)

- `settings_item.xml` - Item de lista para pantalla de configuración
- Layouts estándar de AppCompat (AlertDialog, ActionBar, etc.)

---

## 4. Pseudocódigo de la Funcionalidad Principal

### 4.1 LiveWallpaper Service

```kotlin
// Pseudocódigo de LiveWallpaper
class CosmicFlowWallpaperService : WallpaperService() {
    override fun onCreateEngine(): WallpaperService.Engine {
        return WallpaperEngine()
    }
    
    inner class WallpaperEngine : WallpaperService.Engine(), GLSurfaceView.Renderer {
        private lateinit var glSurfaceView: WallpaperGLSurfaceView
        private lateinit var flowRenderer: FlowRenderer
        
        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            glSurfaceView = WallpaperGLSurfaceView(applicationContext)
            glSurfaceView.eglConfigChooser = WallpaperEGLConfigChooser()
            glSurfaceView.renderer = this
            flowRenderer = FlowRenderer(applicationContext)
        }
        
        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            flowRenderer.onSurfaceCreated()
        }
        
        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            flowRenderer.onSurfaceChanged(width, height)
        }
        
        override fun onDrawFrame(gl: GL10) {
            flowRenderer.onDrawFrame()
        }
        
        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) glSurfaceView.onResume() else glSurfaceView.onPause()
        }
        
        override fun onTouchEvent(event: MotionEvent): Boolean {
            // Manejo de gestos (posible interacción AirTouch)
            return super.onTouchEvent(event)
        }
    }
}
```

### 4.2 FlowRenderer (OpenGL ES 2.0)

```kotlin
// Pseudocódigo de FlowRenderer
class FlowRenderer(context: Context) : GLSurfaceView.Renderer {
    
    private lateinit var flowProgram: ShaderProgram
    private lateinit var backgroundProgram: ShaderProgram
    private lateinit var flowMesh: FlowMeshPrimitive
    private lateinit var flowTexture: Texture
    private var time: Float = 0.0f
    
    // Uniformes del shader de flujo
    private val uTimeNoiseScaleColor = FloatArray(3) // [time, noiseScale, colorIndex]
    private val uPrimaryColor = FloatArray(3)
    private val uSecondaryColor = FloatArray(3)
    private val uNoisePos01 = FloatArray(4)
    
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // Compilar shaders desde res/raw/
        val vertexShaderSource = loadShaderSource("flow_vertex.glsl")
        val fragmentShaderSource = loadShaderSource("flow_fragment.glsl")
        flowProgram = ShaderProgram(vertexShaderSource, fragmentShaderSource)
        
        // Cargar textura
        flowTexture = Texture(context, R.drawable.flow_greyscale)
        
        // Crear malla de flujo
        flowMesh = FlowMeshPrimitive(FLOW_MESH_SIZE)
        
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }
    
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        flowRenderer.matrixSetUp(width, height)
    }
    
    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        
        time += deltaTime
        uTimeNoiseScaleColor[0] = time
        
        // Renderizar fondo
        renderBackground()
        
        // Renderizar flujo
        renderFlow()
    }
    
    private fun renderFlow() {
        flowProgram.use()
        
        // Set uniformes
        flowProgram.setUniform("u_Time_NoiseScale_Color", uTimeNoiseScaleColor)
        flowProgram.setUniform("u_PrimaryColor", uPrimaryColor)
        flowProgram.setUniform("u_SecondaryColor", uSecondaryColor)
        flowProgram.setUniform("u_NoisePos01", uNoisePos01)
        flowProgram.setUniform("uMVPMatrix", mvpMatrix)
        flowProgram.setUniform("sTexture", flowTexture.unit)
        
        // Dibujar malla
        flowMesh.draw()
    }
    
    private fun renderBackground() {
        // Similar, usa background_vertex.glsl y background_fragment.glsl
        // Textura: bg_grey.png con color primario aplicado
    }
}
```

### 4.3 Shader de Flujo (GLSL ES 1.0 - traducido del vertex shader original)

```
attribute vec4 aPosition;
uniform mat4 uMVPMatrix;
uniform vec3 u_Time_NoiseScale_Color; // x=time, y=noiseScale, z=colorIndex
uniform vec4 u_NoisePos01;

varying vec4 vPosition;
varying vec4 vDx;
varying vec4 vDy;

const float POWER_LO = 0.03;
const float POWER_HI = 0.05;
const float TURB = 3.5;
const float SPEED = 0.2;
const float PI_TWICE = 6.2831853;
const vec4 NOISE_SCALE = vec4(0.7, 1.3, 0.7, 1.0);

float wave(float x, vec3 params) {
    return sin(mod(x * params.x + params.y + params.z, PI_TWICE));
}

float addNoiseForAxis(float w1, float w2, float w3, float w4) {
    return POWER_HI * w1 * w2 + POWER_HI * w3 * w4 + POWER_LO * w3 * w2;
}

vec4 addNoise0(vec4 pos) {
    vec2 texPos0 = pos.xy + u_NoisePos01.xy * 3.0;
    float timeSpeedHi = u_Time_NoiseScale_Color.x * SPEED;
    
    // 10 ondas sinusoidales superpuestas para ruido procedural
    float w1 = wave(texPos0.x, vec3(TURB * 5.34, 9.13, timeSpeedHi));
    float w2 = wave(texPos0.y, vec3(TURB * 7.54, 3.33, -timeSpeedHi));
    float w3 = wave(texPos0.x, vec3(TURB * 3.54, 6.13, -timeSpeedHi));
    float w4 = wave(texPos0.y, vec3(TURB * 5.24, 4.33, timeSpeedHi));
    float w5 = wave(texPos0.x, vec3(TURB * 7.12, 5.14, timeSpeedHi));
    float w6 = wave(texPos0.y, vec3(TURB * 1.375, 7.43, -timeSpeedHi));
    float w7 = wave(texPos0.x, vec3(TURB * 3.54, 3.43, -timeSpeedHi));
    float w8 = wave(texPos0.x, vec3(TURB * 2.23, 4.44, timeSpeedHi));
    float w9 = wave(texPos0.y, vec3(TURB * 4.199, 1.84, -timeSpeedHi));
    float w10 = wave(texPos0.x, vec3(TURB * 3.54, 5.13, -timeSpeedHi));
    
    float nx = addNoiseForAxis(w1, w2, w3, w4);
    float ny = nx; // Optimización Mali GPU: ny = nx
    float nz = addNoiseForAxis(w8, w9, w10, w1);
    
    return vec4(pos.x + nx, pos.y + ny, nz, 1.0);
}

void main() {
    float uTime = u_Time_NoiseScale_Color.x;
    float uNoiseScale = u_Time_NoiseScale_Color.y;
    
    gl_Position = uMVPMatrix * addNoise0(aPosition) * NOISE_SCALE;
    
    // Calcular transparencia basada en normal y área
    vDx = delta(aPosition, 0.045, 0.0, gl_Position);
    vDy = delta(aPosition, 0.0, 0.045, gl_Position);
    vPosition = aPosition;
}

// Fragment shader:
uniform vec3 u_PrimaryColor;
uniform vec3 u_SecondaryColor;
uniform sampler2D sTexture;
varying vec4 vPosition;
varying vec4 vDx;
varying vec4 vDy;

void main() {
    float col1 = texture2D(sTexture, vPosition.xy).a;
    float normalAlpha = 1.1 - length(cross(vDx.xyz, vDy.xyz));
    normalAlpha = clamp(normalAlpha, 0.0, 1.0);
    float alpha = normalAlpha * (vPosition.y + 0.2);
    alpha = pow(alpha, 2.3);
    
    gl_FragColor = vec4(u_PrimaryColor + col1 * u_SecondaryColor, alpha);
}
```

### 4.4 Background Shader

```
Vertex: gl_Position = vec4(1.0, u_HeightScale, 1.0, 1.0) * 
         (vPosition + vec4(0.0, -0.2 * sin(vPosition.y + u_XOffset * 2.0 + u_Time + vPosition.x), 0.0, 0.0))
Fragment: gl_FragColor = texture2D(sTexture, texCoord) * vec4(v_Color, 1.0)
```

---

## 5. Recomendaciones para el Clon en Jetpack Compose

### 5.1 Arquitectura sugerida

```kotlin
// CosmicFlowWallpaper.kt - Servicio de wallpaper para Compose
class CosmicFlowWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = CosmicFlowEngine()
}

class CosmicFlowEngine : WallpaperService.Engine() {
    private lateinit var composeView: ComposeView
    private lateinit var coroutineScope: CoroutineScope
    
    override fun onCreate(surfaceHolder: SurfaceHolder) {
        super.onCreate(surfaceHolder)
        composeView = ComposeView(applicationContext)
        // Configurar ComposeView dentro del SurfaceHolder del wallpaper
    }
    
    override fun onVisibilityChanged(visible: Boolean) {
        super.onVisibilityChanged(visible)
        if (visible) coroutineScope.launch { animateFlow() }
        else coroutineScope.cancel()
    }
}
```

### 5.2 Composable principal (Canvas con OpenGL equivalent)

Dado que Jetpack Compose no tiene OpenGL ES nativo, usar `Canvas` con `drawPath`, `drawImage`, y shaders personalizados via `androidx.graphics`:

```kotlin
@Composable
fun CosmicFlowCanvas(
    primaryColor: Color,
    secondaryColor: Color,
    time: Float,
    noiseScale: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Dibujar fondo
        drawBackground(primaryColor)
        
        // Dibujar flujo usando mesh de caminos
        drawFlowMesh(
            time = time,
            noiseScale = noiseScale,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor
        )
    }
}

@Composable
fun FlowAnimation(
    primaryColor: Color,
    secondaryColor: Color
) {
    var time by remember { mutableFloatArrayOf(0f) } // o use rememberCoroutineScope
    LaunchedEffect(Unit) {
        val start = SystemClock.uptimeMillis()
        while (true) {
            time[0] = (SystemClock.uptimeMillis() - start) / 1000f
            delay(16) // ~60 FPS
        }
    }
    
    CosmicFlowCanvas(
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        time = time[0],
        noiseScale = 1.0f
    )
}
```

### 5.3 Constantes a portar (desde GLSL a Kotlin)

```kotlin
object FlowConstants {
    const val POWER_LO = 0.03f
    const val POWER_HI = 0.05f
    const val TURB = 3.5f
    const val SPEED = 0.2f
    const val PI_TWICE = 6.2831853f
    
    // Colores por defecto (extraer de res/color o values/colors.xml)
    val defaultPrimaryColor = Color(0xFF... ) // Extraer de resources
    val defaultSecondaryColor = Color(0xFF...)
    
    // Imágenes de gemas para usar como textura de acento
    val gemDrawables = listOf(
        R.drawable.amber,
        R.drawable.amethyst,
        R.drawable.emerald,
        R.drawable.gold,
        R.drawable.ruby,
        R.drawable.sapphire,
        R.drawable.silk,
        R.drawable.turquoise
    )
}
```

### 5.4 Función de ruido procedural (portada del GLSL)

```kotlin
fun wave(x: Float, params: FloatArray): Float {
    return Math.sin((x * params[0] + params[1] + params[2]) % PI_TWICE).toFloat()
}

fun addNoiseForAxis(w1: Float, w2: Float, w3: Float, w4: Float): Float {
    return POWER_HI * w1 * w2 + POWER_HI * w3 * w4 + POWER_LO * w3 * w2
}

fun addNoise0(posX: Float, posY: Float, time: Float, noisePos01: FloatArray): FloatArray {
    val texPos0x = posX + noisePos01[0] * 3.0f
    val texPos0y = posY + noisePos01[1] * 3.0f
    val timeSpeedHi = time * SPEED
    
    val w1 = wave(texPos0x, floatArrayOf(TURB * 5.34f, 9.13f, timeSpeedHi))
    val w2 = wave(texPos0y, floatArrayOf(TURB * 7.54f, 3.33f, -timeSpeedHi))
    val w3 = wave(texPos0x, floatArrayOf(TURB * 3.54f, 6.13f, -timeSpeedHi))
    val w4 = wave(texPos0y, floatArrayOf(TURB * 5.24f, 4.33f, timeSpeedHi))
    val w5 = wave(texPos0x, floatArrayOf(TURB * 7.12f, 5.14f, timeSpeedHi))
    val w6 = wave(texPos0y, floatArrayOf(TURB * 1.375f, 7.43f, -timeSpeedHi))
    val w7 = wave(texPos0x, floatArrayOf(TURB * 3.54f, 3.43f, -timeSpeedHi))
    val w8 = wave(texPos0x, floatArrayOf(TURB * 2.23f, 4.44f, timeSpeedHi))
    val w9 = wave(texPos0y, floatArrayOf(TURB * 4.199f, 1.84f, -timeSpeedHi))
    val w10 = wave(texPos0x, floatArrayOf(TURB * 3.54f, 5.13f, -timeSpeedHi))
    
    val nx = addNoiseForAxis(w1, w2, w3, w4)
    val ny = nx // Igual que en el original (optimización Mali)
    val nz = addNoiseForAxis(w8, w9, w10, w1)
    
    return floatArrayOf(nx, ny, nz)
}
```

### 5.5 Estructura de archivos sugerida para el clon

```
app/src/main/
├── java/com/yourname/cosmicflow/
│   ├── CosmicFlowWallpaperService.kt   # WallpaperService
│   ├── CosmicFlowEngine.kt              # Engine del wallpaper
│   ├── FlowRendererCompose.kt           # Lógica de renderizado en Compose
│   ├── FlowNoise.kt                     # Funciones de ruido procedural (port desde GLSL)
│   ├── ColorPickerScreen.kt             # Pantalla de selector de colores
│   ├── SettingsScreen.kt                # Pantalla de preferencias
│   ├── ColorInfo.kt                     # Modelo de datos de color
│   └── FlowConstants.kt                 # Constantes (colores, tamaños, etc.)
├── res/
│   ├── drawable/                        # Imágenes de gemas (copiar del APK original)
│   │   ├── amber.png, amethyst.png, emerald.png, gold.png,
│   │   │   ruby.png, sapphire.png, silk.png, turquoise.png
│   │   ├── bg_grey.png
│   │   └── ic_launcher.png
│   ├── values/
│   │   ├── colors.xml                   # Colores primario/secundario por defecto
│   │   ├── strings.xml                  # cosmic_flow_app_name, etc.
│   │   └── dimensions.xml               # FLOW_MESH_SIZE, etc.
│   ├── xml/
│   │   ├── cosmicflow.xml               # Metadata del wallpaper
│   │   └── default_settings.xml         # Preferencias por defecto
│   └── layout/
│       └── settings_item.xml            # Item de lista settings
└── AndroidManifest.xml                  # Declarar WallpaperService, Settings Activity
```

### 5.6 AndroidManifest.xml (parecido al original)

```xml
<manifest package="com.yourname.cosmicflowclone">
    <uses-permission android:name="android.permission.BIND_WALLPAPER" />
    
    <application>
        <!-- Wallpaper Service -->
        <service
            android:name=".CosmicFlowWallpaperService"
            android:permission="android.permission.BIND_WALLPAPER"
            android:label="@string/cosmic_flow_app_name"
            android:icon="@drawable/ic_launcher">
            <intent-filter>
                <action android:name="android.service.wallpaper.WallpaperService" />
            </intent-filter>
            <meta-data
                android:name="android.service.wallpaper"
                android:resource="@xml/cosmicflow" />
        </service>
        
        <!-- Settings Activity -->
        <activity
            android:name=".SettingsScreen"
            android:label="@string/cosmic_flow_settings_title">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

---

## 6. Archivos a Extraer del APK Original para el Clon

| Ruta en APK | Destino en Clon | Uso |
|-------------|----------------|-----|
| `res/drawable/amber.png` | `res/drawable/amber.png` | Textura de gema ámbar |
| `res/drawable/amethyst.png` | `res/drawable/amethyst.png` | Textura de gema amatista |
| `res/drawable/emerald.png` | `res/drawable/emerald.png` | Textura de gema esmeralda |
| `res/drawable/gold.png` | `res/drawable/gold.png` | Textura de gema oro |
| `res/drawable/ruby.png` | `res/drawable/ruby.png` | Textura de gema rubí |
| `res/drawable/sapphire.png` | `res/drawable/sapphire.png` | Textura de gema zafiro |
| `res/drawable/silk.png` | `res/drawable/silk.png` | Textura de gema seda |
| `res/drawable/turquoise.png` | `res/drawable/turquoise.png` | Textura de gema turquesa |
| `res/drawable/bg_grey.png` | `res/drawable/bg_grey.png` | Fondo del wallpaper |
| `res/drawable/ic_launcher.png` | `res/mipmap/ic_launcher.png` | Icono de la app |
| `res/drawable/launcher.png` | `res/mipmap/launcher.png` | Icono launcher alternativo |
| `res/drawable/frame.9.png` | `res/drawable/frame.9.png` | Frame 9-patch |
| `res/raw/flow_vertex.glsl` | Consultar (referencia) | Lógica del vertex shader (portar a Kotlin) |
| `res/raw/flow_fragment.glsl` | Consultar (referencia) | Lógica del fragment shader (portar a Kotlin) |
| `res/raw/background_vertex.glsl` | Consultar (referencia) | Lógica del fondo vertex (portar a Kotlin) |
| `res/raw/background_fragment.glsl` | Consultar (referencia) | Lógica del fondo fragment (portar a Kotlin) |
| `res/raw/noise.png` | `res/raw/noise.png` | Textura de ruido (opcional, 1MB) |
| `res/raw/flow_greyscale.png` | `res/drawable/flow_greyscale.png` | Textura escala grises para flujo |
| `res/xml/cosmicflow.xml` | `res/xml/cosmicflow.xml` | Metadata del wallpaper (decodificar) |
| `res/xml/default_settings.xml` | `res/xml/default_settings.xml` | Preferencias por defecto (decodificar) |

---

## 7. Notas de Implementación

1. **OpenGL ES → Compose Canvas:** Jetpack Compose no ejecuta GLSL directamente. La lógica de ruido procedural del vertex shader debe reescribirse en Kotlin calculando posiciones de vértices en el CPU y dibujando con `Canvas.drawPath()` o usando `androidx.graphics` shaders (API 31+).

2. **Render loop:** Usar `rememberCoroutineScope()` con `delay(16)` para ~60 FPS, o `withFrameNanos` para animación sincronizada con pantalla.

3. **Colores:** Los colores primario/secundario se extraen de SharedPreferences. En Compose, usar `rememberSaveable` o `DataStore` para persistir.

4. **Texturas de gemas:** Las imágenes PNG de gemas pueden usarse como `ImageBitmap` en Compose para aplicar como máscara o textura sobre el flujo.

5. **Wallpaper + Activity:** Un Live Wallpaper necesita tanto el `WallpaperService` como una `Activity` de configuración. El usuario abre la Activity para cambiar colores, y el wallpaper se aplica en segundo plano.

6. **AirTouch/Customization Sony:** Estas son características específicas de Sony Ericsson que no tienen equivalente directo en Android estándar. Se pueden omitir en el clon o reemplazar con gestos de touch en la pantalla.

---

**Fin del informe de ingeniería inversa.**

*Archivos generados durante el análisis:* `extract_strings_v2.ps1`, `strings_classes2.txt` (en `C:\Users\alfon\TGZ\wave\`)
