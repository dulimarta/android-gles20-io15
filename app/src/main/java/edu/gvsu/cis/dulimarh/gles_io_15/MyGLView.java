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
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        gl10.glClear(GLES10.GL_COLOR_BUFFER_BIT | GLES10
                .GL_DEPTH_BUFFER_BIT);
    }
}
