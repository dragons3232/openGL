package com.dragons3232.opengl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

class CameraTexture() {
    private var mProgramObject = 0;
    private var mMVPMatrixHandle = 0;

    private val textureVertices = floatArrayOf(
        0f, 1f,
        1f, 1f,
        0f, 0f,
        1f, 0f
    )
    private var textureBuffer: FloatBuffer? = null
    private var textureHandle = 0

    private val textureIDs = IntArray(1)
    private var bitmap: Bitmap? = null
    private var square = Square();

    private var gl10: GL10? = null;
    private var updated = false;

    fun loadTexture(gl: GL10) {
        gl.glGenTextures(1, textureIDs, 0) // Generate texture-ID array for numFaces IDs

        gl.glBindTexture(GLES30.GL_TEXTURE_2D, textureIDs.get(0))
        gl.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST.toFloat()
        )
        gl.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR.toFloat()
        )
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap?.recycle()
    }

    fun prepare(context: Context?, unused: GL10) {
        gl10 = unused
        val vShaderStr = ("#version 300 es 			  \n"
                + "in vec4 vPosition;           \n"
                + "uniform mat4 uMVPMatrix;     \n"
                + "in vec2 vertexUV;"
                + "out vec2 UV;"
                + "void main()                  \n"
                + "{                            \n"
                + "   gl_Position = uMVPMatrix * vPosition;  \n"
                + "UV = vertexUV;"
                + "}                            \n")

        val fShaderStr = ("#version 300 es		 			          	\n"
                + "precision mediump float;					  	\n"
                + "uniform vec4 u_Color; \n"
                + "in vec2 UV;"
                + "out vec4 fragColor;	 			 		  	\n"
                + "uniform sampler2D uTexture;"
                + "void main()                                  \n"
                + "{                                            \n"
                + "  fragColor = texture( uTexture, UV);	\n"
                + "}                                            \n")

        val vertexShader: Int
        val fragmentShader: Int
        val programObject: Int
        val linked = IntArray(1)

        // Load the vertex/fragment shaders
        vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vShaderStr)
        fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr)

        // Create the program object
        programObject = GLES30.glCreateProgram()

        if (programObject == 0) {
            return
        }

        GLES30.glAttachShader(programObject, vertexShader)
        GLES30.glAttachShader(programObject, fragmentShader)

        // Bind vPosition to attribute 0
        GLES30.glBindAttribLocation(programObject, 0, "vPosition")

        // Link the program
        GLES30.glLinkProgram(programObject)

        // Get access to projection matrix. Must call after linking program
        textureHandle = GLES30.glGetUniformLocation(programObject, "uTexture")
        mMVPMatrixHandle = GLES30.glGetUniformLocation(programObject, "uMVPMatrix");

        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0)

        if (linked[0] == 0) {
            GLES30.glDeleteProgram(programObject)
            return
        }

        // Store the program object
        mProgramObject = programObject

        var buff = ByteBuffer.allocateDirect(textureVertices.size * 4)
        buff.order(ByteOrder.nativeOrder())
        textureBuffer = buff.asFloatBuffer()
        textureBuffer?.put(textureVertices)
        textureBuffer?.position(0)

        bitmap = BitmapFactory.decodeResource(
            context?.getResources(),
            R.drawable.vn
        )
        loadTexture(unused)
    }

    fun updateTexture(bitmap: Bitmap) {
        this.bitmap = bitmap;
        updated = true;
    }

    fun draw(vPMatrix: FloatArray) {
        // Use the program object
        GLES30.glUseProgram(mProgramObject);

        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, vPMatrix, 0)

        if (updated) {
            this.gl10?.glDeleteTextures(textureIDs.size, textureIDs, 0);
            loadTexture(this.gl10!!)
            updated = false;
        }
        GLES30.glVertexAttribPointer(textureIDs[0], 2, GLES30.GL_FLOAT, false, 0, textureBuffer)
        GLES30.glEnableVertexAttribArray(textureIDs[0]);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIDs[0]);
        square.draw()
        GLES30.glDisableVertexAttribArray(textureIDs[0]);
    }
}