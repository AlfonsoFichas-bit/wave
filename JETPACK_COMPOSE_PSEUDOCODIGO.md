# 🎨 Pseudocódigo Jetpack Compose - Clon de Cosmic Flow

**Base:** Ingeniería inversa de `Cosmic_Flow_S.apk` (package `com.sonyericsson.cosmicflow`, v1.1)
**Objetivo:** Guía de implementación 1:1 para Jetpack Compose (no es código listo para compilar, es pseudocódigo estructurado)

---

## 1. Arquitectura General

```
app/
├── java/com/tuapp/cosmicflow/
│   ├── CosmicFlowWallpaperService.kt   # WallpaperService (punto de entrada Android)
│   ├── CosmicFlowEngine.kt             # Engine del wallpaper (ciclo de vida)
│   ├── CosmicFlowRenderer.kt           # Lógica de renderizado (port de FlowRenderer)
│   ├── FlowNoise.kt                    # Algoritmo de ruido procedural (port del GLSL)
│   ├── FlowMesh.kt                     # Geometría de malla (port de MeshPrimitive)
│   ├── ColorPicker.kt                  # Selector de colores (port de ColorPicker)
│   ├── ColorInfo.kt                    # Modelo de datos de color (port de ColorInfo)
│   ├── SettingsScreen.kt               # Pantalla de configuración (port de Settings)
│   ├── Customization.kt                # Integración Sony (opcional, omitir en clon genérico)
│   └── FlowConstants.kt                # Constantes (port de Config)
├── res/
│   ├── drawable/                       # Imágenes de gemas (copiar del APK original)
│   ├── xml/
│   │   ├── cosmicflow.xml              # Metadata del wallpaper
│   │   └── default_settings.xml        # Preferencias por defecto
│   └── values/
│       ├── colors.xml                  # Colores primario/secundario
│       ├── strings.xml                 # Textos de la app
│       └── dimensions.xml              # Dimensiones
└── AndroidManifest.xml                 # Declarar WallpaperService + Settings Activity
```

---

## 2. Constantes (port de `com.sonyericsson.cosmicflow.gl.Config`)

```kotlin
object FlowConstants {
    // Animación
    const val BASE_ANIMATION_SPEED = 0.6f
    const val MAX_ANIMATION_SPEED_FACTOR = 1.0f
    const val MIN_ANIMATION_SPEED_FACTOR = 0.5f
    const val MAX_DELTA_FRAME_TIME = 83.333336f
    const val MIDDLE_DELTA_FRAME_TIME = 50.0f
    const val MIN_DELTA_FRAME_TIME = 33.333332f
    const val LOWER_FPS_TIME_INTERVAL = 3000.0f
    const val LOWER_SPEED_TIME_INTERVAL = 6000.0f
    const val LOWEST_FPS_TIME_INTERVAL = 9000.0f
    const val LOWEST_SPEED_TIME_INTERVAL = 10000.0f
    const val TOUCH_ACCELERATION_DURATION = 3000.0f
    const val ON_TOUCH_EXTRA_SPEED = 0.078f
    const val ON_TOUCH_DELTA_AMPLITUDE = 2

    // Malla
    const val FLOW_MESH_SIZE = 100  // short

    // Rotación del fondo
    const val X_ROTATION = 90.0f
    var Z_ROTATION: Float = 0.0f  // mutable en el original

    // Texturas
    const val BLUR_TEX_WIDTH = 1.0f
    const val BLUR_TEX_HEIGHT = 1.0f

    // Debug
    const val GLOBAL_DEBUG = false
    const val LOG_FPS = false
    const val TAG = "CosmicFlowWallpaper"

    // Sony UXP (opcional, para clon genérico omitir)
    const val SEMC_RESOURCE_PACKAGE = "com.sonyericsson.uxp"
}
```

---

## 3. Algoritmo de Ruido Procedural (port del GLSL `flow_vertex.glsl`)

Este es el corazón del efecto visual. El vertex shader original usa 10 ondas sinusoidales superpuestas para deformar la malla:

```kotlin
object FlowNoise {
    private const val POWER_LO = 0.03f
    private const val POWER_HI = 0.05f
    private const val TURB = 3.5f
    private const val SPEED = 0.2f
    private const val PI_TWICE = 6.2831853f  // 2 * PI
    private val NOISE_SCALE = floatArrayOf(0.7f, 1.3f, 0.7f, 1.0f)

    /**
     * Función wave del GLSL: sin(mod(x * params[0] + params[1] + params[2], PI_TWICE))
     */
    private fun wave(x: Float, params: FloatArray): Float {
        return Math.sin((x * params[0] + params[1] + params[2]) % PI_TWICE).toFloat()
    }

    /**
     * addNoiseForAxis del GLSL
     */
    private fun addNoiseForAxis(w1: Float, w2: Float, w3: Float, w4: Float): Float {
        return POWER_HI * w1 * w2 + POWER_HI * w3 * w4 + POWER_LO * w3 * w2
    }

    /**
     * addNoise0 del GLSL - calcula el desplazamiento de ruido para una posición
     * Retorna [nx, ny, nz]
     */
    fun addNoise0(
        posX: Float,
        posY: Float,
        time: Float,
        noisePos01: FloatArray  // [x, y, z, w] - solo se usan x, y
    ): FloatArray {
        val texPos0x = posX + noisePos01[0] * 3.0f
        val texPos0y = posY + noisePos01[1] * 3.0f
        val timeSpeedHi = time * SPEED

        // 10 ondas sinusoidales superpuestas (exactamente como en el GLSL)
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
        val ny = nx  // En el GLSL original: ny = nx (optimización Mali GPU)
        val nz = addNoiseForAxis(w8, w9, w10, w1)

        return floatArrayOf(nx, ny, nz)
    }

    /**
     * Calcula el desplazamiento de la malla (delta) para un vértice dado
     * Port de la función delta() del GLSL
     */
    fun delta(
        posX: Float,
        posY: Float,
        dx: Float,
        dy: Float,
        time: Float,
        noisePos01: FloatArray,
        mvpMatrix: FloatArray,  // 16 floats, matriz 4x4
        newPos: FloatArray      // [x, y, z, w] resultado previo
    ): FloatArray {
        val offsetPos = floatArrayOf(posX + dx, posY + dy, 0.0f, 0.0f)
        val noiseResult = addNoise0(offsetPos[0], offsetPos[1], time, noisePos01)

        // uMVPMatrix * addNoise0(...) * NOISE_SCALE - newPos
        // Simplificado: aplicar escala de ruido y restar newPos
        val resultX = noiseResult[0] * NOISE_SCALE[0] - newPos[0]
        val resultY = noiseResult[1] * NOISE_SCALE[1] - newPos[1]
        val resultZ = noiseResult[2] * NOISE_SCALE[2] - newPos[2]

        return floatArrayOf(resultX, resultY, resultZ, 1.0f)
    }
}
```

---

## 4. Modelo de Datos de Color (port de `ColorInfo`)

```kotlin
data class ColorInfo(
    val name: String,
    val color: Long,      // color empaquetado como long (ARGB)
    val thumbnail: Int    // resource ID de la imagen de gema
) {
    companion object {
        private const val MASK_32BITS = 4294967295L

        // Colores predefinidos (extraer valores reales de los drawables del APK)
        val Amber = ColorInfo("Amber", 0xFFFFBF00, R.drawable.amber)
        val Amethyst = ColorInfo("Amethyst", 0xFF9966CC, R.drawable.amethyst)
        val Emerald = ColorInfo("Emerald", 0xFF00A86B, R.drawable.emerald)
        val Gold = ColorInfo("Gold", 0xFFFFD700, R.drawable.gold)
        val Ruby = ColorInfo("Ruby", 0xFFE0115F, R.drawable.ruby)
        val Sapphire = ColorInfo("Sapphire", 0xFF0F52BA, R.drawable.sapphire)
        val Silk = ColorInfo("Silk", 0xFFE6BE8A, R.drawable.silk)
        val Turquoise = ColorInfo("Turquoise", 0xFF40E0D0, R.drawable.turquoise)

        val allColors = listOf(Amber, Amethyst, Emerald, Gold, Ruby, Sapphire, Silk, Turquoise)

        /**
         * Extrae componentes RGBA de un color long (port de extract())
         * Retorna [[r, g, b, a], [r2, g2, b2, a2]]... (float[][] en el original)
         */
        fun extract(): FloatArray {
            val color = this.color
            return floatArrayOf(
                ((color shr 24) and 0xFF).toFloat() / 255f,  // R
                ((color shr 16) and 0xFF).toFloat() / 255f,  // G
                ((color shr 8) and 0xFF).toFloat() / 255f,   // B
                (color and 0xFF).toFloat() / 255f            // A
            )
        }

        /**
         * Convierte color int ARGB a float[] [r, g, b, a] (port de rgbIntToFloatArray())
         */
        fun rgbIntToFloatArray(argb: Int): FloatArray {
            return floatArrayOf(
                ((argb shr 16) and 0xFF).toFloat() / 255f,
                ((argb shr 8) and 0xFF).toFloat() / 255f,
                (argb and 0xFF).toFloat() / 255f,
                ((argb shr 24) and 0xFF).toFloat() / 255f
            )
        }

        /**
         * Convierte bytes RGB a int (port de rgbBytesToInt())
         */
        fun rgbBytesToInt(r: Int, g: Int, b: Int): Int {
            return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        }
    }
}
```

---

## 5. Selector de Colores (port de `ColorPicker`)

```kotlin
class ColorPicker(context: Context) {
    private val mContext = context
    private lateinit var mPreferences: SharedPreferences
    private var mCurrentColor: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)  // primario
    private var mSecondaryColor: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)  // secundario
    private val sColors: Array<ColorInfo> = ColorInfo.allColors.toTypedArray()

    init {
        mPreferences = context.getSharedPreferences("cosmicFlowPrefs", Context.MODE_PRIVATE)
        loadColors()
    }

    private fun loadColors() {
        // Cargar colores desde SharedPreferences (clave: "preferences_color")
        val storedColor = mPreferences.getInt("preferences_color", ColorInfo.Amber.color.toInt())
        mCurrentColor = ColorInfo.rgbIntToFloatArray(storedColor)
    }

    fun getPrimaryColor(): FloatArray = mCurrentColor
    fun getSecondaryColor(): FloatArray = mSecondaryColor

    fun setPrimaryColor(color: FloatArray) {
        mCurrentColor = color
        val argb = Color.rgb(
            (color[0] * 255).toInt(),
            (color[1] * 255).toInt(),
            (color[2] * 255).toInt()
        )
        mPreferences.edit().putInt("preferences_color", argb).apply()
    }

    fun setSecondaryColor(color: FloatArray) {
        mSecondaryColor = color
    }

    fun getAllColors(): Array<ColorInfo> = sColors

    fun destroy() {
        // Limpiar recursos si es necesario
    }

    inner class C00001 : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            if (key == "preferences_color") {
                loadColors()
            }
        }
    }
}
```

---

## 6. Renderizador Principal (port de `FlowRenderer`)

Esta es la clase más importante. Implementa el bucle de renderizado OpenGL ES 2.0. En Jetpack Compose, se traduce a un `Canvas` con `drawIntoCanvas` o `AndroidView` con `GLSurfaceView`.

### Opción A: Usando Canvas de Compose (recomendado para simplicidad)

```kotlin
class CosmicFlowRenderer(
    context: Context,
    colorPicker: ColorPicker
) {
    private val context = context
    private val colorPicker = colorPicker
    private var mAnimationTimeSeconds: Float = 0.0f
    private var mAnimationTimeMillis: Float = 0.0f
    private var mLastFrameTime: Float = 0.0f
    private var mLastAnimationTimeUpdate: Float = 0.0f
    private var mAnimationSpeedFactor: Float = FlowConstants.BASE_ANIMATION_SPEED
    private var mIsTouched: Boolean = false
    private var mLastTouchTime: Float = 0.0f
    private var mTimeTouchBonus: Float = 0.0f
    private var mIsFirstFrame: Boolean = true
    private var mDestroyed: Boolean = false

    // Matrices (port de mMVPMatrix, mMMatrix, mVMatrix, mProjMatrix)
    private val mMVPMatrix = FloatArray(16)
    private val mProjMatrix = FloatArray(16)
    private val mVMatrix = FloatArray(16)
    private val mMMatrix = FloatArray(16)

    // Viewport
    private var mViewportWidth: Int = 0
    private var mViewportHeight: Int = 0

    // Texturas (IDs de OpenGL o ImageBitmap en Compose)
    private var mFlowBitmap: Bitmap? = null
    private var mFlowTex: Int = 0
    private var mBackgroundBitmap: Bitmap? = null
    private var mNoiseBitmap: Bitmap? = null
    private var mNoiseScale: Float = 1.0f

    // Colores actuales
    private val sCurrentPrimaryColor = FloatArray(4)
    private val sCurrentSecondaryColor = FloatArray(4)

    // Malla de flujo
    private var mFlowVertexBufferObject: VertexBufferObject? = null
    private var mSquareVertexBufferObject: VertexBufferObject? = null

    // Shaders (en Compose se usan shaders personalizados o se simula con Canvas)
    private var mFlowVertexShader: String = ""
    private var mFlowFragmentShader: String = ""
    private var mBackgroundVertexShader: String = ""
    private var mBackgroundFragmentShader: String = ""

    // Frame lock para sincronización
    private val mFrameLock = Any()

    init {
        createShaders()
        createTextures()
        matrixSetUp()
    }

    private fun createShaders() {
        // Cargar shaders desde res/raw/ (en Compose, se leen como recursos de texto)
        mFlowVertexShader = loadShaderResource(R.raw.flow_vertex)
        mFlowFragmentShader = loadShaderResource(R.raw.flow_fragment)
        mBackgroundVertexShader = loadShaderResource(R.raw.background_vertex)
        mBackgroundFragmentShader = loadShaderResource(R.raw.background_fragment)
    }

    private fun loadShaderResource(resId: Int): String {
        return context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
    }

    private fun createTextures() {
        // Cargar bitmaps desde recursos
        mFlowBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.flow_greyscale)
        mBackgroundBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bg_grey)
        mNoiseBitmap = BitmapFactory.decodeResource(context.resources, R.raw.noise)
    }

    fun matrixSetUp() {
        // Configurar matriz de proyección ortográfica
        // En Compose Canvas, esto se maneja automáticamente con el sistema de coordenadas
        val ratio = mViewportWidth.toFloat() / mViewportHeight.toFloat()
        // Matriz ortográfica simplificada
        Matrix.setIdentityM(mProjMatrix, 0)
        Matrix.orthoM(mProjMatrix, 0, -1f, 1f, -1f, 1f, -1f, 1f)
        Matrix.setIdentityM(mVMatrix, 0)
        Matrix.setIdentityM(mMMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mMMatrix, 0)
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        mViewportWidth = width
        mViewportHeight = height
        matrixSetUp()
    }

    fun onDrawFrame(deltaTime: Float) {
        synchronized(mFrameLock) {
            if (mDestroyed) return

            // Actualizar tiempo de animación
            mAnimationTimeMillis += deltaTime
            mAnimationTimeSeconds = mAnimationTimeMillis / 1000.0f

            // Actualizar velocidad si hay toque
            updateSpeed()

            // Actualizar colores desde ColorPicker
            updateColorPickerColors()

            // Renderizar fondo
            renderBackground()

            // Renderizar flujo
            renderFlow()
        }
    }

    private fun updateSpeed() {
        val currentTime = System.currentTimeMillis().toFloat()
        var diff = currentTime - mLastAnimationTimeUpdate
        mLastAnimationTimeUpdate = currentTime

        // Limitar delta time (port de MAX_DELTA_FRAME_TIME, etc.)
        diff = coerceIn(diff, FlowConstants.MIN_DELTA_FRAME_TIME, FlowConstants.MAX_DELTA_FRAME_TIME)

        // Ajustar velocidad basado en FPS (port de la lógica original)
        if (diff > FlowConstants.LOWEST_FPS_TIME_INTERVAL) {
            mAnimationSpeedFactor = FlowConstants.MIN_ANIMATION_SPEED_FACTOR
        } else if (diff > FlowConstants.LOWER_FPS_TIME_INTERVAL) {
            mAnimationSpeedFactor = FlowConstants.MIN_ANIMATION_SPEED_FACTOR + 0.1f
        } else {
            mAnimationSpeedFactor = FlowConstants.BASE_ANIMATION_SPEED
        }

        // Bonus de velocidad por toque
        if (mIsTouched) {
            mTimeTouchBonus += diff * FlowConstants.ON_TOUCH_EXTRA_SPEED
            if (mTimeTouchBonus > FlowConstants.TOUCH_ACCELERATION_DURATION) {
                mIsTouched = false
                mTimeTouchBonus = 0.0f
            }
        }
    }

    private fun updateColorPickerColors() {
        val primary = colorPicker.getPrimaryColor()
        val secondary = colorPicker.getSecondaryColor()
        sCurrentPrimaryColor.set(primary, 0, 4)
        sCurrentSecondaryColor.set(secondary, 0, 4)
    }

    private fun renderBackground() {
        // En Compose Canvas: dibujar fondo con textura bg_grey y color primario aplicado
        // Port de background_vertex.glsl + background_fragment.glsl
        // El fondo se desplaza verticalmente con seno basado en el tiempo
    }

    private fun renderFlow() {
        // Port de renderFlow() + draw de FlowMeshPrimitive
        // Para cada vértice de la malla:
        // 1. Calcular ruido con FlowNoise.addNoise0()
        // 2. Aplicar desplazamiento al vértice
        // 3. Calcular transparencia basada en la normal (producto cruz de vDx, vDy)
        // 4. Aplicar color primario + secundario según textura
        // 5. Dibujar con Canvas.drawPath() o drawImage()
    }

    fun onTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsTouched = true
                mLastTouchTime = System.currentTimeMillis().toFloat()
            }
            MotionEvent.ACTION_UP -> {
                mIsTouched = false
            }
        }
    }

    fun onOffsetsChanged(xOffset: Float, yOffset: Float, xStep: Float, yStep: Float, xPixels: Int, yPixels: Int) {
        // Manejar desplazamiento de pantalla (wallpaper scrolling)
    }

    fun onVisibilityChanged(visible: Boolean) {
        if (!visible) {
            mDestroyed = true
        }
    }

    fun destroy() {
        mDestroyed = true
        // Liberar texturas, shaders, etc.
    }

    // Setters para colores (llamados desde ColorPicker)
    fun setPrimaryColor(color: FloatArray) {
        sCurrentPrimaryColor.set(color, 0, 4)
    }

    fun setSecondaryColor(color: FloatArray) {
        sCurrentSecondaryColor.set(color, 0, 4)
    }
}
```

### Opción B: VertexBufferObject y Mesh (port de `VertexBufferObject` + `MeshPrimitive`)

```kotlin
class VertexBufferObject(private val mesh: MeshPrimitive) {
    private var mVertexBuffer: Int = 0  // ID de OpenGL o referencia en Compose
    private var mIndexBuffer: Int = 0
    private var mIndexSize: Int = 0
    private var mTextureBuffer: FloatBuffer? = null

    fun getVertexBuffer(): Int = mVertexBuffer
    fun getIndexBuffer(): Int = mIndexBuffer
    fun getIndexSize(): Int = mIndexSize
    fun getTextureBuffer(): FloatBuffer? = mTextureBuffer

    fun destroy() {
        // Liberar buffers
    }
}

abstract class MeshPrimitive {
    companion object {
        const val FLOAT_SIZE = 4
        const val SHORT_SIZE = 2
    }

    var mVertexBuffer: FloatBuffer? = null
    var mIndexBuffer: ShortBuffer? = null
    var mTextureBuffer: FloatBuffer? = null
    var mVertexNumBytes: Int = 0
    var mIndexNumBytes: Int = 0
    var mIndices: Int = 0
    var mIsTextured: Boolean = false

    abstract fun getVertexBuffer(): FloatBuffer?
    abstract fun getIndexBuffer(): ShortBuffer?
    abstract fun getTextureBuffer(): FloatBuffer?
    abstract fun getVertexBufferSize(): Int
    abstract fun getIndexBufferSize(): Int
    abstract fun getNumIndices(): Int
    abstract fun isTextured(): Boolean

    companion object {
        fun newFlowPrimitive(size: Short): MeshPrimitive = FlowMeshPrimitive(size)
        fun newSquareMeshPrimitive(width: Float, height: Float): MeshPrimitive = SquareMeshPrimitive(width, height)
    }
}

class FlowMeshPrimitive(private val size: Short) : MeshPrimitive() {
    // Generar malla de vértices para el flujo ondulante
    // En el original: malla 2D que se deforma con ruido
    init {
        // Crear vertex buffer, index buffer, texture buffer
        // tamaño: size x size vértices
    }

    override fun getVertexBuffer(): FloatBuffer? = mVertexBuffer
    override fun getIndexBuffer(): ShortBuffer? = mIndexBuffer
    override fun getTextureBuffer(): FloatBuffer? = mTextureBuffer
    override fun getVertexBufferSize(): Int = mVertexNumBytes
    override fun getIndexBufferSize(): Int = mIndexNumBytes
    override fun getNumIndices(): Int = mIndices
    override fun isTextured(): Boolean = mIsTextured
}

class SquareMeshPrimitive(private val width: Float, private val height: Float) : MeshPrimitive() {
    // Malla cuadrada para el fondo
    init {
        // Crear cuadrado simple de 4 vértices
    }

    override fun getVertexBuffer(): FloatBuffer? = mVertexBuffer
    override fun getIndexBuffer(): ShortBuffer? = mIndexBuffer
    override fun getTextureBuffer(): FloatBuffer? = mTextureBuffer
    override fun getVertexBufferSize(): Int = mVertexNumBytes
    override fun getIndexBufferSize(): Int = mIndexNumBytes
    override fun getNumIndices(): Int = mIndices
    override fun isTextured(): Boolean = mIsTextured
}
```

---

## 7. Wallpaper Engine (port de `LiveWallpaper` + `WallpaperEngine`)

```kotlin
class CosmicFlowWallpaperService : WallpaperService() {
    override fun onCreateEngine(): WallpaperService.Engine {
        return CosmicFlowEngine()
    }
}

class CosmicFlowEngine : WallpaperService.Engine() {
    private lateinit var composeView: ComposeView
    private lateinit var flowRenderer: CosmicFlowRenderer
    private lateinit var colorPicker: ColorPicker
    private var animationCoroutine: Job? = null

    override fun onCreate(surfaceHolder: SurfaceHolder) {
        super.onCreate(surfaceHolder)

        // Inicializar ColorPicker
        colorPicker = ColorPicker(applicationContext)

        // Inicializar Renderizador
        flowRenderer = CosmicFlowRenderer(applicationContext, colorPicker)

        // Configurar ComposeView dentro del SurfaceHolder del wallpaper
        composeView = ComposeView(applicationContext)
        composeView.setContent {
            CosmicFlowScreen(
                renderer = flowRenderer,
                colorPicker = colorPicker,
                onTouch = { event -> flowRenderer.onTouchEvent(event) }
            )
        }

        // Vincular ComposeView al SurfaceHolder (requiere integración personalizada)
        // Nota: En un Live Wallpaper real, se necesita un bridge entre SurfaceHolder y ComposeView
    }

    override fun onVisibilityChanged(visible: Boolean) {
        super.onVisibilityChanged(visible)
        if (visible) {
            startAnimationLoop()
        } else {
            stopAnimationLoop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAnimationLoop()
        flowRenderer.destroy()
        colorPicker.destroy()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        flowRenderer.onTouchEvent(event)
        return true
    }

    override fun onOffsetsChanged(
        xOffset: Float, yOffset: Float,
        xOffsetStep: Float, yOffsetStep: Float,
        xPixelOffset: Int, yPixelOffset: Int
    ) {
        flowRenderer.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset)
    }

    private fun startAnimationLoop() {
        val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
        animationCoroutine = coroutineScope.launch {
            var lastTime = System.currentTimeMillis().toFloat()
            while (isVisible) {
                val currentTime = System.currentTimeMillis().toFloat()
                val deltaTime = currentTime - lastTime
                lastTime = currentTime

                flowRenderer.onDrawFrame(deltaTime)
                composeView.invalidate()  // Forzar redibujado

                delay(16)  // ~60 FPS
            }
        }
    }

    private fun stopAnimationLoop() {
        animationCoroutine?.cancel()
        animationCoroutine = null
    }
}
```

---

## 8. Pantalla Compose Principal

```kotlin
@Composable
fun CosmicFlowScreen(
    renderer: CosmicFlowRenderer,
    colorPicker: ColorPicker,
    onTouch: (MotionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var time by remember { mutableFloatArrayOf(0f) }

    LaunchedEffect(Unit) {
        val start = SystemClock.uptimeMillis()
        while (true) {
            time[0] = (SystemClock.uptimeMillis() - start) / 1000f
            delay(16)
        }
    }

    Canvas(
        modifier = modifier.fillMaxSize().pointerInput(Unit) {
            detectTapGestures { offset ->
                // Convertir offset a MotionEvent y llamar a onTouch
            }
        }
    ) {
        // Dibujar fondo
        drawBackground(renderer)

        // Dibujar flujo
        drawFlow(renderer, time[0])
    }
}

@Composable
fun drawBackground(renderer: CosmicFlowRenderer) {
    // Usar drawImage con bg_grey.png y aplicar color primario
    // En el GLSL original: background_fragment multiplica textura por v_Color
}

@Composable
fun drawFlow(renderer: CosmicFlowRenderer, time: Float) {
    // Para cada vértice de la malla de flujo:
    // 1. Calcular ruido con FlowNoise.addNoise0()
    // 2. Crear Path con los vértices desplazados
    // 3. Calcular alpha basado en la normal (producto cruz)
    // 4. Dibujar con drawPath usando color primario + secundario
}
```

---

## 9. Pantalla de Configuración (port de `Settings`)

```kotlin
@Composable
fun SettingsScreen(
    colorPicker: ColorPicker,
    onColorChange: (ColorInfo) -> Unit
) {
    var selectedColor by remember { mutableStateOf(ColorInfo.Amber) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Cosmic Flow Settings",
            style = MaterialTheme.typography.h5
        )

        Divider()

        Text("Seleccionar color:")
        LazyColumn {
            items(colorPicker.getAllColors()) { colorInfo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onColorChange(colorInfo) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImageBitmap(
                        imageBitmap = colorInfo.thumbnail.toImageBitmap(),
                        contentDescription = colorInfo.name,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(text = colorInfo.name)
                    if (selectedColor == colorInfo) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            }
        }
    }
}
```

---

## 10. AndroidManifest.xml

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.tuapp.cosmicflow">

    <uses-permission android:name="android.permission.BIND_WALLPAPER" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/cosmic_flow_app_name"
        android:theme="@style/AppTheme">

        <service
            android:name=".CosmicFlowWallpaperService"
            android:permission="android.permission.BIND_WALLPAPER"
            android:label="@string/cosmic_flow_app_name"
            android:icon="@mipmap/ic_launcher">
            <intent-filter>
                <action android:name="android.service.wallpaper.WallpaperService" />
            </intent-filter>
            <meta-data
                android:name="android.service.wallpaper"
                android:resource="@xml/cosmicflow" />
        </service>

        <activity
            android:name=".SettingsScreen"
            android:label="@string/cosmic_flow_settings_title"
            android:icon="@mipmap/ic_launcher"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>
</manifest>
```

---

## 11. Archivo XML del Wallpaper (`res/xml/cosmicflow.xml`)

```xml
<?xml version="1.0" encoding="utf-8"?>
<wallpaper xmlns:android="http://schemas.android.com/apk/res/android"
    android:thumbnail="@drawable/ic_launcher"
    android:description="@string/cosmic_flow_description"
    android:settingsActivity="com.tuapp.cosmicflow.SettingsScreen" />
```

---

## 12. Assets a Copiar del APK Original

| Archivo en APK | Destino en Clon | Uso |
|----------------|-----------------|-----|
| `res/drawable/amber.png` | `res/drawable/amber.png` | Gema ámbar |
| `res/drawable/amethyst.png` | `res/drawable/amethyst.png` | Gema amatista |
| `res/drawable/emerald.png` | `res/drawable/emerald.png` | Gema esmeralda |
| `res/drawable/gold.png` | `res/drawable/gold.png` | Gema oro |
| `res/drawable/ruby.png` | `res/drawable/ruby.png` | Gema rubí |
| `res/drawable/sapphire.png` | `res/drawable/sapphire.png` | Gema zafiro |
| `res/drawable/silk.png` | `res/drawable/silk.png` | Gema seda |
| `res/drawable/turquoise.png` | `res/drawable/turquoise.png` | Gema turquesa |
| `res/drawable/bg_grey.png` | `res/drawable/bg_grey.png` | Fondo principal |
| `res/drawable/flow_greyscale.png` | `res/drawable/flow_greyscale.png` | Textura escala grises |
| `res/raw/noise.png` | `res/raw/noise.png` | Textura de ruido (1 MB) |
| `res/drawable/ic_launcher.png` | `res/mipmap/ic_launcher.png` | Icono de la app |

---

## 13. Notas de Implementación

1. **OpenGL ES → Compose Canvas:** Jetpack Compose no ejecuta GLSL directamente. El algoritmo de ruido (`FlowNoise.addNoise0()`) debe calcularse en CPU y los vértices se dibujan con `Canvas.drawPath()`.

2. **Render Loop:** Usar `LaunchedEffect` con `delay(16)` para ~60 FPS, o `withFrameNanos` para sincronización con pantalla.

3. **Colores:** Persistir con `DataStore` o `SharedPreferences` (clave: `"preferences_color"`).

4. **Texturas de gemas:** Las imágenes PNG de gemas se usan como `ImageBitmap` para mostrar en el selector de colores.

5. **Wallpaper + Activity:** Un Live Wallpaper necesita tanto el `WallpaperService` como una `Activity` de configuración.

6. **AirTouch/Sony Customization:** Estas son características específicas de Sony Ericsson. Se pueden omitir en un clon genérico o reemplazar con gestos de touch en pantalla.

---

## 14. Archivos Generados durante el Análisis

- `C:\Users\alfon\TGZ\wave\COSMIC_FLOW_REVERSE_ENGINEERING.md` - Informe completo de ingeniería inversa
- `C:\Users\alfon\TGZ\wave\strings_classes2.txt` - Strings extraídos del DEX
- `C:\Users\alfon\TGZ\wave\cosmic_extract/content/` - Contenido extraído del APK

---

**Fin del pseudocódigo**
