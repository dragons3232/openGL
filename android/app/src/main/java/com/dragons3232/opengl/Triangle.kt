package com.dragons3232.opengl

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/*
* A triangle with 3 vertices.
*/
class Triangle {
    private val vertexBuffer: FloatBuffer // Buffer for vertex-array
    private val indexBuffer: ByteBuffer // Buffer for index-array
    private val vertices = floatArrayOf( // Vertices of the triangle
        0.0f, 1.0f, 0.0f,  // 0. top
        -1.0f, -1.0f, 0.0f,  // 1. left-bottom
        1.0f, -1.0f, 0.0f // 2. right-bottom
    )
    private val indices = byteArrayOf(0, 1, 2) // Indices to above vertices (in CCW)

    // Constructor - Setup the data-array buffers
    init {
        // Setup vertex-array buffer. Vertices in float. A float has 4 bytes.
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder()) // Use native byte order
        vertexBuffer = vbb.asFloatBuffer() // Convert byte buffer to float
        vertexBuffer.put(vertices) // Copy data into buffer
        vertexBuffer.position(0) // Rewind

        // Setup index-array buffer. Indices in byte.
        indexBuffer = ByteBuffer.allocateDirect(indices.size)
        indexBuffer.put(indices)
        indexBuffer.position(0)
    }

    // Render this shape
    fun draw() {
        // Load the vertex data
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glEnableVertexAttribArray(0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
    }
}