# 🌌 Cosmic Flow Clone - Jetpack Compose

Clon del Live Wallpaper **CosmicFlow** (`com.sonyericsson.cosmicflow`, v1.1) reimplementado en **Jetpack Compose** basado en ingeniería inversa del APK original.

## 📋 Descripción

Esta aplicación es un **papel tapiz animado (Live Wallpaper)** que renderiza un efecto de "flujo cósmico" en tiempo real. El efecto consiste en una malla de vértices deformada por ruido procedural (10 ondas sinusoidales superpuestas) con transparencia basada en la normal de la superficie, colores primario/secundario configurables, y texturas de gemas (ámbar, amatista, esmeralda, oro, rubí, zafiro, seda, turquesa).

---

## 📁 Estructura de Directorios

### `app/src/main/java/com/tuapp/cosmicflow/`

Código fuente principal de la aplicación en Kotlin.

| Archivo | Propósito |
|---------|-----------|
| `CosmicFlowWallpaperService.kt` | Servicio de wallpaper (`WallpaperService`). Punto de entrada del sistema Android. Declara el `BIND_WALLPAPER` permission y crea el `Engine`. |
| `CosmicFlowEngine.kt` | Engine del wallpaper. Gestiona el ciclo de vida (visible/hidden), touch events, offsets de pantalla y el bucle de animación con corrutinas (~60 FPS). |
| `CosmicFlowRenderer.kt` | Lógica de renderizado (port de `FlowRenderer`). Gestiona tiempo de animación, matrices de proyección, shaders, texturas y el llamado a `renderFlow()` y `renderBackground()`. |
| `FlowNoise.kt` | **Core del efecto visual.** Implementa el algoritmo de ruido procedural traducido del GLSL (`flow_vertex.glsl`). Contiene `wave()`, `addNoiseForAxis()`, `addNoise0()` y `delta()`. |
| `FlowMesh.kt` | Geometría de malla (port de `MeshPrimitive` + `FlowMeshPrimitive` + `SquareMeshPrimitive`). Genera buffers de vértices, índices y texturas para la malla ondulante y el fondo cuadrado. |
| `VertexBufferObject.kt` | Gestión de buffers OpenGL (port de `VertexBufferObject`). Wraps de `FloatBuffer` y `ShortBuffer` para vértices, índices y coordenadas de textura. |
| `ColorPicker.kt` | Selector de colores (port de `ColorPicker`). Carga colores desde `SharedPreferences`, gestiona color primario y secundario, y notifica cambios. |
| `ColorInfo.kt` | Modelo de datos de color (port de `ColorInfo`). Define las 8 gemas predefinidas con sus colores ARGB y thumbnails. Incluye métodos `extract()`, `rgbIntToFloatArray()`, `rgbBytesToInt()`. |
| `SettingsScreen.kt` | Pantalla de configuración (port de `Settings`). UI en Compose para seleccionar el color de la gema y guardar preferencias. |
| `FlowConstants.kt` | Todas las constantes del port de `Config`. Velocidad de animación, tamaño de malla (`FLOW_MESH_SIZE=100`), intervalos de FPS, rotación del fondo, etc. |
| `Customization.kt` | Integración con Sony Customization provider (opcional). Puede omitirse en un clon genérico. |

### `app/src/main/res/drawable/`

Recursos de imagen extraídos del APK original.

| Archivo | Origen | Uso |
|---------|--------|-----|
| `amber.png` | APK original | Textura de gema ámbar (selector de colores) |
| `amethyst.png` | APK original | Textura de gema amatista |
| `emerald.png` | APK original | Textura de gema esmeralda |
| `gold.png` | APK original | Textura de gema oro |
| `ruby.png` | APK original | Textura de gema rubí |
| `sapphire.png` | APK original | Textura de gema zafiro |
| `silk.png` | APK original | Textura de gema seda |
| `turquoise.png` | APK original | Textura de gema turquesa |
| `bg_grey.png` | APK original | Fondo principal del wallpaper (175 KB) |
| `flow_greyscale.png` | APK original | Textura en escala de grises para peso de color del flujo |
| `ic_launcher.png` | APK original | Icono de la aplicación |

> ⚠️ **Nota:** Copia estos archivos desde `cosmic_extract/content/res/drawable/` del análisis de ingeniería inversa.

### `app/src/main/res/drawable-*-dpi/`

Variantes de densidad de pantalla para los iconos y drawables (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi).

### `app/src/main/res/mipmap/`

Iconos del launcher en diferentes densidades.

### `app/src/main/res/xml/`

Archivos de configuración XML.

| Archivo | Propósito |
|---------|-----------|
| `cosmicflow.xml` | Metadata del wallpaper (thumbnail, descripción, activity de settings). Copiado del APK original. |
| `default_settings.xml` | Valores por defecto de preferencias (color inicial, etc.). |

### `app/src/main/res/raw/`

Recursos crudos.

| Archivo | Propósito |
|---------|-----------|
| `noise.png` | Textura de ruido (~1 MB). Usada para el desplazamiento de vértices. |

### `app/src/main/res/values/`

| Archivo | Propósito |
|---------|-----------|
| `colors.xml` | Colores primario y secundario por defecto (extraídos del APK). |
| `strings.xml` | Textos de la app (`cosmic_flow_app_name`, `cosmic_flow_settings_title`, etc.). |
| `dimens.xml` | Dimensiones (tamaño de malla, grosor de líneas, etc.). |

### `app/src/main/res/layout/`

| Archivo | Propósito |
|---------|-----------|
| `settings_item.xml` | Layout para items de la lista de configuración. |

---

## 🎯 Objetivos del Clon

1. **Reproducir el efecto visual 1:1** - El flujo cósmico ondulante con ruido procedural idéntico al original (misma matemática de ondas sinusoidales, mismas constantes).
2. **Mantener la arquitectura de Live Wallpaper** - `WallpaperService` + `Engine` + `Settings Activity`.
3. **Soportar selección de colores de gema** - Las 8 gemas con sus colores y thumbnails originales.
4. **Persistir preferencias** - Color seleccionado guardado en `SharedPreferences` (`cosmicFlowPrefs`, clave `preferences_color`).
5. **Reaccionar a toque y offsets de pantalla** - Aceleración por toque y desplazamiento al cambiar de pantalla principal.

---

## 🔧 Cómo Usar los Hallazgos de Ingeniería Inversa

### Archivos de referencia generados:

| Archivo | Ruta | Contenido |
|---------|------|-----------|
| Informe de REA | `COSMIC_FLOW_REVERSE_ENGINEERING.md` | Análisis completo: manifiesto, 541 clases, shaders GLSL, assets, arquitectura |
| Pseudocódigo Compose | `JETPACK_COMPOSE_PSEUDOCODIGO.md` | Código estructurado listo para implementar en Jetpack Compose |

### Pasos de implementación:

1. **Copiar assets** - Extraer imágenes de gemas, fondo y texturas del APK original a `res/drawable/`.
2. **Crear `FlowConstants.kt`** - Usar las constantes de `Config` del APK (velocidad, malla, FPS).
3. **Implementar `FlowNoise.kt`** - Traducir el algoritmo del GLSL `flow_vertex.glsl` a Kotlin (ya incluido en el pseudocódigo).
4. **Crear `CosmicFlowRenderer.kt`** - Port de `FlowRenderer` usando `Canvas` de Compose.
5. **Crear `ColorPicker.kt` + `ColorInfo.kt`** - Selector de colores con las 8 gemas.
6. **Crear `CosmicFlowWallpaperService.kt` + `CosmicFlowEngine.kt`** - Estructura de wallpaper.
7. **Crear `SettingsScreen.kt`** - Pantalla de configuración en Compose.
8. **Configurar `AndroidManifest.xml`** - Declarar `WallpaperService` con `BIND_WALLPAPER` y `Settings Activity`.

---

## ⚠️ Limitaciones Técnicas

- **OpenGL ES → Compose Canvas:** Jetpack Compose no ejecuta GLSL nativamente. El ruido procedural se calcula en CPU con `FlowNoise.addNoise0()` y se dibuja con `Canvas.drawPath()`.
- **AirTouch / Sony Customization:** Son características específicas de Sony Ericsson. Se omiten en el clon genérico o se reemplazan con gestos de touch estándar.
- **Shaders GLSL:** Los archivos `.glsl` del `res/raw/` son referencia para la matemática, no se ejecutan directamente en Compose.

---

## 📦 Dependencias Sugeridas (build.gradle.kts)

```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.foundation:foundation:1.7.0")
    implementation("androidx.compose.material:material:1.7.0")
    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.0")
    implementation("androidx.datastore:datastore-preferences:1.1.0")
}
```

---

## 📜 Licencia

Este clon está basado en ingeniería inversa de `Cosmic_Flow_S.apk` (package `com.sonyericsson.cosmicflow`). Los assets originales (imágenes de gemas, fondo, texturas) pertenecen a sus respectivos autores. El código de implementación en Jetpack Compose es nuevo y derivado del análisis de la estructura y comportamiento del original.

---

## 🗺️ Mapa de Archivos Generados en este Análisis

```
C:\Users\alfon\TGZ\wave\
├── COSMIC_FLOW_REVERSE_ENGINEERING.md   # Informe completo de ingeniería inversa
├── JETPACK_COMPOSE_PSEUDOCODIGO.md       # Pseudocódigo para Jetpack Compose
├── README.md                              # Este archivo
├── cosmic_extract/                        # Contenido extraído del APK
│   └── content/
│       ├── res/
│       ├── classes.dex
│       ├── classes2.dex
│       ├── AndroidManifest.xml
│       └── resources.arsc
├── strings_classes2.txt                   # Strings extraídos del DEX
└── extract_strings_v2.ps1                 # Script de extracción de strings
```

---

**Fin del README**
