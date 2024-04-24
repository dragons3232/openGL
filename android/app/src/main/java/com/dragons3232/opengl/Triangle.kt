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
    private var indexBufferId = 0
    private val vertices = floatArrayOf( // Vertices of the triangle
        0.0f, 1.0f, 0.0f,  // 0. top
        -1.0f, -1.0f, 0.0f,  // 1. left-bottom
        1.0f, -1.0f, 0.0f, // 2. right-bottom
    )
    private val indices = byteArrayOf(0, 1, 2, 0) // Indices to above vertices (in CCW)

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

    fun bindBuffers() {
        val buffers = IntArray(1)
        GLES30.glGenBuffers(1, buffers, 0);

        indexBufferId = buffers[0]

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        GLES30.glBufferData(
            GLES30.GL_ELEMENT_ARRAY_BUFFER,
            indices.size,
            indexBuffer,
            GLES30.GL_STATIC_DRAW
        );
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    // Render this shape
    fun draw() {
        // Load the vertex data
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glEnableVertexAttribArray(0);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBufferId)
        GLES30.glDrawElements(GLES30.GL_LINE_STRIP, indices.size, GLES30.GL_UNSIGNED_BYTE, 0)

        // reuse same index buffer to draw filled triangle
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.size - 1, GLES30.GL_UNSIGNED_BYTE, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}