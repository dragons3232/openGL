import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.dragons3232.opengl.CameraTexture
import com.dragons3232.opengl.Square
import com.dragons3232.opengl.Triangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

fun loadShader(type: Int, shaderSrc: String): Int {
    val shader: Int
    val compiled = IntArray(1)

    // Create the shader object
    shader = GLES30.glCreateShader(type)
    if (shader == 0) {
        return 0
    }

    // Load the shader source
    GLES30.glShaderSource(shader, shaderSrc)

    // Compile the shader
    GLES30.glCompileShader(shader)

    // Check the compile status
    GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
    if (compiled[0] == 0) {
        GLES30.glDeleteShader(shader)
        return 0
    }
    return shader
}


class MyGLRenderer(context: Context?) : GLSurfaceView.Renderer {
    var mProgramObject = 0;
    var triangle = Triangle();
    var square = Square();
    var camTexture: CameraTexture? = null;

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private var mMVPMatrixHandle = 0
    private var mColorHandle = 0

    private val rotationMatrix = FloatArray(16)
    private val zoomMatrix = FloatArray(16)

    private var context: Context?

    init {
        this.context = context
    }

    fun onCamera(bitmap: Bitmap) {
        camTexture?.updateTexture(bitmap)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        val vShaderStr = ("#version 300 es 			  \n"
                + "in vec4 vPosition;           \n"
                + "uniform mat4 uMVPMatrix;     \n"
                + "void main()                  \n"
                + "{                            \n"
                + "   gl_Position = uMVPMatrix * vPosition;  \n"
                + "}                            \n")

        val fShaderStr = ("#version 300 es		 			          	\n"
                + "precision mediump float;					  	\n"
                + "uniform vec4 u_Color; \n"
                + "out vec4 fragColor;	 			 		  	\n"
                + "void main()                                  \n"
                + "{                                            \n"
                + "  fragColor = u_Color;	\n"
                + "}                                            \n")

        val vertexShader: Int
        val fragmentShader: Int
        val programObject: Int
        val linked = IntArray(1)

        // Load the vertex/fragment shaders

        // Load the vertex/fragment shaders
        vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vShaderStr)
        fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr)

        // Create the program object

        // Create the program object
        programObject = GLES30.glCreateProgram()

        if (programObject == 0) {
            return
        }

        GLES30.glAttachShader(programObject, vertexShader)
        GLES30.glAttachShader(programObject, fragmentShader)

        // Bind vPosition to attribute 0

        // Bind vPosition to attribute 0
        GLES30.glBindAttribLocation(programObject, 0, "vPosition")

        // Link the program

        // Link the program
        GLES30.glLinkProgram(programObject)

        // Get access to projection matrix. Must call after linking program
        mMVPMatrixHandle = GLES30.glGetUniformLocation(programObject, "uMVPMatrix");
        mColorHandle = GLES30.glGetUniformLocation(programObject, "u_Color");

        // Check the link status

        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0)

        if (linked[0] == 0) {
            GLES30.glDeleteProgram(programObject)
            return
        }

        // Store the program object

        // Store the program object
        mProgramObject = programObject
        // Set the background frame color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        camTexture = CameraTexture()
        camTexture?.prepare(context, unused)
        triangle.bindBuffers()
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        // Use the program object
        GLES30.glUseProgram(mProgramObject);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0.1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, vPMatrix, 0)

        camTexture?.draw(vPMatrix)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        val rratio = 1 / ratio
        val left = -ratio
        val right = ratio
        val bottom = -1.0f
        val top = 1.0f
        val near = 1.0f
        val far = 10.0f

        if (ratio >= 1.0f)
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 0.1f, 10.0f); //Landscape
        else
            Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -rratio, rratio, 0.1f, 10.0f); //Portrait
        camTexture?.updateRatio(ratio)
    }
}