package edu.gvsu.cis.dulimarh.gles_io_15;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dulimarh on 5/27/15.
 */
public class MyGLView extends GLSurfaceView implements GLSurfaceView
        .Renderer {

    private Triangle tr_one;

    public MyGLView(Context context) {
        super(context);
        init();
    }

    public MyGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        gl10.glClearColor(0.05f, 0.203f, 0.42f, 0.0f);
        tr_one = new Triangle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        float ratio;
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        if (width > height) {
            ratio = (float) width/height;
            gl10.glOrthof(-ratio, ratio, -1.0f, +1.0f, -1.0f, +1.0f);
        }
        else {
            ratio = (float) height/width;
            gl10.glOrthof(-1.0f, +1.0f, -ratio, ratio, -1.0f, +1.0f);
        }
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        gl10.glClear(GLES10.GL_COLOR_BUFFER_BIT | GLES10
                .GL_DEPTH_BUFFER_BIT);
        gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl10.glEnableClientState(GL10.GL_COLOR_ARRAY);
        tr_one.draw();
    }
}
