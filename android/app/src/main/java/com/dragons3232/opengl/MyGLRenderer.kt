import android.opengl.GLES30
import android.opengl.GLSurfaceView
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


class MyGLRenderer : GLSurfaceView.Renderer {
    var mProgramObject = 0;
    var triangle = Triangle();
    var square = Square();

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        val vShaderStr = ("#version 300 es 			  \n"
                + "in vec4 vPosition;           \n"
                + "void main()                  \n"
                + "{                            \n"
                + "   gl_Position = vPosition;  \n"
                + "}                            \n")

        val fShaderStr = ("#version 300 es		 			          	\n"
                + "precision mediump float;					  	\n"
                + "out vec4 fragColor;	 			 		  	\n"
                + "void main()                                  \n"
                + "{                                            \n"
                + "  fragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );	\n"
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
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        // Use the program object
        GLES30.glUseProgram ( mProgramObject );

//        triangle.draw()
        square.draw()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }
}