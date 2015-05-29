package edu.gvsu.cis.dulimarh.gles_io_15;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dulimarh on 5/27/15.
 */
public class MyGLView extends GLSurfaceView implements GLSurfaceView
        .Renderer {

    private static final float TRI_SPEED = 0.012f; /* 12 degs/sec */
    private static final float TORUS_SPEED = 0.060f; /* 60 degs/sec */
    private Triangle tri_one;
    private Torus tor_one;
    private float[] tri_cf, torus_cf;
    private long lastMilliSec;
    private float tri_angle, tri_scale;
    public MyGLView(Context context) {
        super(context);
        init();
    }

    public MyGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        tri_cf = new float[16];
        torus_cf = new float[16];
        Matrix.setIdentityM(tri_cf, 0);
        Matrix.setIdentityM(torus_cf, 0);
        setRenderer(this);
    }

    private void updateCoordFrames(long now, long delta)
    {
        //Matrix.rotateM(tri_cf, 0, TRI_SPEED * delta, 0, 0, 1);
        tri_angle += TRI_SPEED * delta;
        tri_scale = 0.8f + 0.2f * (float) Math.cos(now/160);
        Matrix.setRotateM(tri_cf, 0, tri_angle, 0, 0, 1);
        Matrix.scaleM(tri_cf, 0, tri_scale, tri_scale, 1);

        Matrix.rotateM(torus_cf, 0, -TORUS_SPEED * delta, 0, 0, 1);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        gl10.glClearColor(0.05f, 0.203f, 0.42f, 0.0f);
        tri_one = new Triangle();
        tor_one = new Torus(0.6f, 0.15f, 30, 20, 270);

        gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        lastMilliSec = System.currentTimeMillis();
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
        long now = System.currentTimeMillis();
        updateCoordFrames(now, now - lastMilliSec);
        lastMilliSec = now;
        gl10.glClear(GLES10.GL_COLOR_BUFFER_BIT | GLES10
                .GL_DEPTH_BUFFER_BIT);
        gl10.glEnableClientState(GL10.GL_COLOR_ARRAY);
        
        gl10.glPushMatrix();
        gl10.glMultMatrixf(tri_cf, 0);
        tri_one.draw();
        gl10.glPopMatrix();

        gl10.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl10.glColor4f(0.6f, 0.6f, 0.2f, 1.0f);
        gl10.glPushMatrix();
        gl10.glMultMatrixf(torus_cf, 0);
        tor_one.draw();
        gl10.glPopMatrix();
    }
}
