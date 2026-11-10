package com.buga.cosmic

import com.buga.cosmic.gl.CosmicMatrixState
import com.buga.cosmic.gl.CosmicMesh
import com.buga.cosmic.theme.CosmicTheme
import org.junit.Assert.*
import org.junit.Test

class CosmicFlowUnitTest {

    @Test
    fun testAllThemesDefinedAndValidColors() {
        val themes = CosmicTheme.getAllThemes()
        assertEquals("Should define exactly 7 Sony Xperia themes", 7, themes.size)

        val expectedNames = setOf("Amethyst", "Turquoise", "Emerald", "Sapphire", "Gold", "Silk", "Ruby")
        val actualNames = themes.map { it.displayName }.toSet()
        assertEquals(expectedNames, actualNames)

        for (theme in themes) {
            assertEquals("${theme.name} primaryColor should have 3 RGB floats", 3, theme.primaryColor.size)
            assertEquals("${theme.name} secondaryColor should have 3 RGB floats", 3, theme.secondaryColor.size)

            for (c in theme.primaryColor) {
                assertTrue("${theme.name} primary channel $c out of bounds [0, 1]", c in 0.0f..1.0f)
            }
            for (c in theme.secondaryColor) {
                assertTrue("${theme.name} secondary channel $c out of bounds [0, 1]", c in 0.0f..1.0f)
            }
        }

        assertNotNull(CosmicTheme.DEFAULT)
        assertEquals(CosmicTheme.AMETHYST, CosmicTheme.DEFAULT)
    }

    @Test
    fun testCosmicMeshBufferSizesAndBounds() {
        val resX = 48
        val resY = 48
        val mesh = CosmicMesh(resX, resY)

        val expectedVertices = resX * resY
        assertEquals(expectedVertices * 4, mesh.vertexBuffer.capacity())
        assertEquals(expectedVertices * 2, mesh.texCoordBuffer.capacity())

        val expectedQuads = (resX - 1) * (resY - 1)
        val expectedIndices = expectedQuads * 6
        assertEquals(expectedIndices, mesh.indexCount)
        assertEquals(expectedIndices, mesh.indexBuffer.capacity())

        // Check positions
        mesh.vertexBuffer.position(0)
        for (i in 0 until expectedVertices) {
            val x = mesh.vertexBuffer.get()
            val y = mesh.vertexBuffer.get()
            val z = mesh.vertexBuffer.get()
            val w = mesh.vertexBuffer.get()

            assertTrue("x coordinate $x should be between -1.0 and 1.0", x in -1.0f..1.0f)
            assertTrue("y coordinate $y should be between -1.0 and 1.0", y in -1.0f..1.0f)
            assertEquals("z coordinate should be 0.0", 0.0f, z, 0.0001f)
            assertEquals("w coordinate should be 1.0", 1.0f, w, 0.0001f)
        }

        // Check UVs
        mesh.texCoordBuffer.position(0)
        for (i in 0 until expectedVertices) {
            val u = mesh.texCoordBuffer.get()
            val v = mesh.texCoordBuffer.get()
            assertTrue("u $u should be in [0, 1]", u in 0.0f..1.0f)
            assertTrue("v $v should be in [0, 1]", v in 0.0f..1.0f)
        }
    }

    @Test
    fun testCosmicMatrixStateParallax() {
        val matrixState = CosmicMatrixState()
        matrixState.updateParallax(0.5f)
        assertEquals(0.0f, matrixState.currentParallaxOffset, 0.001f)

        matrixState.updateParallax(0.0f)
        assertEquals(-1.0f, matrixState.currentParallaxOffset, 0.001f)

        matrixState.updateParallax(1.0f)
        assertEquals(1.0f, matrixState.currentParallaxOffset, 0.001f)
    }
}
