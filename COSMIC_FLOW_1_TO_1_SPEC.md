# ESPECIFICACIÓN TÉCNICA 1:1 DE SONY COSMIC FLOW
## Documento Maestro de Ingeniería Inversa y Reproducción Exacta

Este documento contiene la extracción y análisis forense del código fuente original de Sony Ericsson (`Cosmic_Flow_S.apk` / `com.sonyericsson.cosmicflow.gl`), explicando exactamente por qué la versión inicial difirió visualmente y documentando la implementación 1:1 matemática y gráfica exacta para su reproducción fidedigna.

---

## 1. Diagnóstico: ¿Por qué la versión inicial difirió del original?

| Componente | Versión Inicial (Jules) | Original Sony Xperia (1:1) | Impacto Visual |
|---|---|---|---|
| **Orientación de la Malla** | Rotación en X de `15.0°` | Rotación en X de **`90.0°`** | Al rotar solo 15°, la malla se ve como una pared vertical de frente. Al rotarla 90°, queda **completamente horizontal** extendiéndose hacia el horizonte. |
| **Escala de la Malla** | `scaleM(1.0, 1.0, 1.0)` | **`scaleM(6.0, 4.0, 7.0)`** | En Sony está estirada enormemente a lo ancho (6.0) y en profundidad Z (7.0), creando el efecto de velo infinito. |
| **Cámara y Frustum** | `perspectiveM(45°)` con cámara en `(0, -1.5, 2.2)` | **`frustumM(-ratio, ratio, -1, 1, 2.5, 10)`** con cámara en **`(0, 0, -5.0)`** | La cámara de Sony está alejada en -5.0 con frustum cerrado (near 2.5, far 10.0), aplanando la perspectiva para darle suavidad de cinta 2D/3D. |
| **Rango de Vértices** | `[-1.0, 1.0]` (centrado) | **`[0.0, 1.0]` en X**, **`[0.0, 0.7]` en Y** | La relación de aspecto 1.0 : 0.7 comprime la malla verticalmente antes de la deformación. |
| **Modo de Primitiva GL** | `GL_TRIANGLES` (modo 4) | **`GL_TRIANGLE_STRIP`** (modo 5) con reinicio degenerado | `GL_TRIANGLE_STRIP` produce superficies continuas y fluidas sin aristas facetadas. |
| **Movimiento del Ruido** | Lineal (`time * 0.1f`, `time * 0.05f`) | **Combinación armónica de 3 senos/cosenos** | El ruido en Sony no avanza en línea recta: describe una órbita senoidal suave a 3 frecuencias distintas. |

---

## 2. Código Original Decompilado de Sony (Referencia 1:1)

### 2.1 Configuración de Matrices (`FlowRenderer.matrixSetUp`)
```java
private void matrixSetUp() {
    Matrix.setIdentityM(this.mMMatrix, 0);
    Matrix.rotateM(this.mMMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f); // 90° en X
    Matrix.rotateM(this.mMMatrix, 0, 0.0f, 0.0f, 0.0f, 1.0f);
    Matrix.scaleM(this.mMMatrix, 0, 6.0f, 4.0f, 7.0f);          // Escala (6.0, 4.0, 7.0)
    Matrix.translateM(this.mMMatrix, 0, this.mXOffset - 0.47f, -0.5f, 0.0f);
    Matrix.multiplyMM(this.mMVPMatrix, 0, this.mVMatrix, 0, this.mMMatrix, 0);
    Matrix.multiplyMM(this.mMVPMatrix, 0, this.mProjMatrix, 0, this.mMVPMatrix, 0);
}
```

### 2.2 Cámara y Proyección (`FlowRenderer.onSurfaceChanged`)
```java
@Override
public void onSurfaceChanged(GL10 glUnused, int width, int height) {
    this.mViewportHeight = height;
    this.mViewportWidth = width;
    GLES20.glViewport(0, 0, this.mViewportWidth, this.mViewportHeight);

    if (this.mViewportWidth <= this.mViewportHeight) {
        float ratio = ((float) this.mViewportWidth) / ((float) this.mViewportHeight);
        Matrix.frustumM(this.mProjMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 2.5f, 10.0f);
    } else {
        float ratio2 = ((float) this.mViewportHeight) / ((float) this.mViewportWidth);
        Matrix.frustumM(this.mProjMatrix, 0, -0.7f, 0.7f, -ratio2, ratio2, 2.5f, 10.0f);
    }

    // Cámara fija mirando al origen desde -5.0f en Z
    Matrix.setLookAtM(this.mVMatrix, 0, 0.0f, 0.0f, -5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
    matrixSetUp();
}
```

### 2.3 Generación de Malla de Flujo (`FlowMeshPrimitive`)

#### Vértices:
```java
private static FloatBuffer vertices(int width) {
    int size = width * width;
    float[] vertices = new float[size * 3]; // 3 componentes por vértice (x, y, z)
    for (int i = 0; i < width; i++) {
        int idx = i * width * 3;
        int idx2 = idx;
        int idx3 = 0;
        while (idx3 < width) {
            vertices[idx2 + 0] = (idx3 * 1.0f) / width;  // X en [0.0, 1.0]
            vertices[idx2 + 1] = (i * 0.7f) / width;     // Y en [0.0, 0.7] (¡Factor 0.7!)
            vertices[idx2 + 2] = 0.0f;                   // Z = 0.0
            idx3++;
            idx2 += 3;
        }
    }
    FloatBuffer buffer = ByteBuffer.allocateDirect(size * 3 * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    buffer.put(vertices);
    buffer.position(0);
    return buffer;
}
```

#### Índices con Triangle Strip y Vértices Degenerados:
```java
private static ShortBuffer indices(short width) {
    short idxSizeStrip = (short) ((width * 2) + 2);
    int totalIndices = (short) ((width - 1) * idxSizeStrip);
    short[] indices = new short[totalIndices];

    for (short i2 = 0; i2 < width - 1; i2 = (short) (i2 + 1)) {
        int idx2 = (short) (i2 * idxSizeStrip);
        for (short j = 0; j < width; j = (short) (j + 1)) {
            short s = (short) (idx2 + 1);
            indices[idx2] = (short) ((i2 * width) + j);
            idx2 = (short) (s + 1);
            indices[s] = (short) (((i2 + 1) * width) + j);
        }
        // Vértices degenerados para unir las filas sin saltos visibles
        short s2 = (short) (idx2 + 1);
        indices[idx2] = (short) ((((i2 + 1) * width) + width) - 1);
        indices[s2] = (short) ((i2 + 1) * width);
    }

    ShortBuffer buffer = ByteBuffer.allocateDirect(totalIndices * 2)
        .order(ByteOrder.nativeOrder())
        .asShortBuffer();
    buffer.put(indices);
    buffer.position(0);
    return buffer;
}
```

---

### 2.4 Renderizado del Flujo (`FlowRenderer.renderFlow`)

```java
private void renderFlow() {
    this.mFlowProgram.use();
    
    // 1. Tiempo escalado al 60%
    float timeSec = this.mAnimationTimeSeconds * 0.6f;
    GLES20.glUniform3f(u_Time_NoiseScale_Color, timeSec, this.mNoiseScale, this.mColor);
    GLES20.glUniformMatrix4fv(matrixHandleUniform, 1, false, this.mMVPMatrix, 0);

    // 2. Trayectoria senoidal armónica exacta de Sony para el ruido
    float noisePos0X = 0.45f + (((((float) Math.cos(((double) this.mAnimationTimeSeconds) * 0.02d * 0.6d)) * 0.32f)
                              + (((float) Math.sin(((double) this.mAnimationTimeSeconds) * 0.01d * 0.6d)) * 0.1f)
                              + (((float) Math.sin(((double) this.mAnimationTimeSeconds) * 0.005d * 0.6d)) * 0.07f)) * 0.7f);

    float noisePos0Y = (((((float) Math.sin(((double) this.mAnimationTimeSeconds) * 0.02d * 0.6d)) * 0.3f)
                        + (((float) Math.cos(((double) this.mAnimationTimeSeconds) * 0.01d * 0.6d)) * 0.1f)
                        + (((float) Math.cos(((double) this.mAnimationTimeSeconds) * 0.005d * 0.6d)) * 0.07f)) * 0.7f) + 0.5f;

    GLES20.glUniform4f(u_NoisePos01, noisePos0X, noisePos0Y, 0.0f, 0.0f);

    // 3. Colores primario y secundario
    GLES20.glUniform3f(u_PrimaryColor, sCurrentPrimaryColor[0], sCurrentPrimaryColor[1], sCurrentPrimaryColor[2]);
    GLES20.glUniform3f(u_SecondaryColor, sCurrentSecondaryColor[0], sCurrentSecondaryColor[1], sCurrentSecondaryColor[2]);

    // 4. Textura de flujo (flow_greyscale.png en unidad 0)
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.mFlowTex.getId());
    GLES20.glUniform1i(tex0, 0);

    // 5. Dibujar usando GL_TRIANGLE_STRIP (modo 5)
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.mFlowVertexBufferObject.getVertexBuffer());
    GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
    GLES20.glEnableVertexAttribArray(posHandle);

    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, this.mFlowVertexBufferObject.getIndexBuffer());
    GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, this.mFlowVertexBufferObject.getIndexSize(), GLES20.GL_UNSIGNED_SHORT, 0);
}
```

---

### 2.5 Renderizado del Fondo Ondulante (`SquareMeshPrimitive` + `renderBackground`)

#### Geometría del Fondo:
```java
// Quad de fondo: 4 vértices
float[] vertices = {-width, height, 0.02f,  width, height, 0.02f,  -width, -height, 0.02f,  width, -height, 0.02f};
float[] texCoords = {0.0f, 0.0f,  1.0f, 0.0f,  0.0f, 1.0f,  1.0f, 1.0f};
short[] indices = {0, 1, 2, 1, 2, 3};
```

#### Uniforms del Fondo:
```java
// u_Time_HeightScale_Color_XOffset:
// x: time * 0.6f
// y: 1.0f / (noiseScale / 8.0f) -> con noiseScale=8.0f da 1.0f
// z: colorIndex
// w: xOffset (desplazamiento del launcher Sony)
GLES20.glUniform4f(
    u_Time_HeightScale_Color_XOffset,
    this.mAnimationTimeSeconds * 0.6f,
    1.0f / (this.mNoiseScale / 8.0f),
    this.mColor,
    this.mXOffset
);
```

---

## 3. Hoja de Ruta para la Implementación 1:1

Cuando se decida implementar la réplica 1:1:
1. **Crear una variante de Renderer (`CosmicFlowNativeRenderer.kt`)** conservando la versión Crystal actual en el proyecto como alternativa estética.
2. **Reemplazar la matriz de modelo:** Aplicar la rotación de 90° en X y el escalado `(6.0, 4.0, 7.0)`.
3. **Reemplazar la cámara:** Usar `Matrix.setLookAtM(..., 0f, 0f, -5f, 0f, 0f, 0f, 0f, 1f, 0f)` y `Matrix.frustumM`.
4. **Actualizar la malla:** Construir los vértices con `(x * 1.0f) / width`, `(y * 0.7f) / width`, `vec3` y `GL_TRIANGLE_STRIP`.
5. **Aplicar las fórmulas senoidales armónicas exactas de Sony** para `u_NoisePos01`.
