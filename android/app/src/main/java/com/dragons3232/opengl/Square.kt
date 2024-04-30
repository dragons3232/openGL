package com.dragons3232.opengl

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/*
* A square drawn in 2 triangles (using TRIANGLE_STRIP).
*/
class Square {
    private var vertexBuffer: FloatBuffer // Buffer for vertex-array
    private val vertices = floatArrayOf( // Vertices for the square
        -1.0f, -1.0f, 0.0f,  // 0. left-bottom
        1.0f, -1.0f, 0.0f,  // 1. right-bottom
        -1.0f, 1.0f, 0.0f,  // 2. left-top
        1.0f, 1.0f, 0.0f // 3. right-top
    )

    // Constructor - Setup the vertex buffer
    init {
        // Setup vertex array buffer. Vertices in float. A float has 4 bytes
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder()) // Use native byte order
        vertexBuffer = vbb.asFloatBuffer() // Convert from byte to float
        vertexBuffer.put(vertices) // Copy data into buffer
        vertexBuffer.position(0) // Rewind
    }

    fun updateRatio(ratio: Float) {
        for (i in 0..3) {
            vertices[i * 3 + 1] /= ratio
        }
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder()) // Use native byte order
        vertexBuffer = vbb.asFloatBuffer() // Convert from byte to float
        vertexBuffer.put(vertices) // Copy data into buffer
        vertexBuffer.position(0) // Rewind
    }

    // Render the shape
    fun draw() {
        // Load the vertex data
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glEnableVertexAttribArray(0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, vertices.size / 3);
    }
}