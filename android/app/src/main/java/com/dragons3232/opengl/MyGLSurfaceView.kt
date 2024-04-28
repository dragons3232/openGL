import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView

internal class MyGLSurfaceView(context: Context?) : GLSurfaceView(context) {
    private val renderer: MyGLRenderer

    init {

        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(3)
        renderer = MyGLRenderer(context)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
    }

    fun consumeCamera(bitmap: Bitmap) {
        renderer?.onCamera(bitmap)
    }
}