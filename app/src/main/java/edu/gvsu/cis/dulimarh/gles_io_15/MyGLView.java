package edu.gvsu.cis.dulimarh.gles_io_15;

import android.content.Context;
import android.opengl.GLES20;
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

    private static final float TRI_SPEED = 0.120f;
    private static final float TORUS_SPEED = 0.06f;
    private Triangle tri_one;
    private Torus tor_one;
    private float[] tri_cf, torus_cf;
    private float[] projectionMat, modelviewMat, tmpMat;
    private long lastMilliSec;
    private float tri_angle, tri_scale;
    private Context context;
    private Shader shNoColor, shColorArray;
    public MyGLView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MyGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        tri_cf = new float[16];
        torus_cf = new float[16];
        Matrix.setIdentityM(tri_cf, 0);
        Matrix.setIdentityM(torus_cf, 0);

        setEGLContextClientVersion(2); /* Use OpenGL ES 2.0 */
        setRenderer(this);
        projectionMat = new float[16];
        modelviewMat = new float[16];
        tmpMat = new float[16];
    }

    private void updateCoordFrames(long now, long delta)
    {
        Matrix.setIdentityM(tri_cf, 0);
        Matrix.translateM(tri_cf, 0, 0.6f, 0, 0);
        tri_angle += TRI_SPEED * delta;
        tri_scale = 0.6f + 0.2f * (float) Math.cos(now/160);
        Matrix.rotateM(tri_cf, 0, tri_angle, 0, 0, 1);
        Matrix.scaleM(tri_cf, 0, tri_scale, tri_scale, 1);

        Matrix.rotateM(torus_cf, 0, -TORUS_SPEED * delta, 0, 0, 1);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.05f, 0.203f, 0.42f, 0.0f);
        tri_one = new Triangle();
        tor_one = new Torus(0.6f, 0.15f, 30, 20, 270);
        shNoColor = new Shader(context, "vs_no_color.shdr", "fs_fixed.shdr");
        shColorArray = new Shader(context, "vs_color_array.shdr", "fs_passthru.shdr");

        //GLES20.glEnableClientState(GLES20.GL_VERTEX_ARRAY);
        lastMilliSec = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        float ratio;
        Matrix.setIdentityM(projectionMat, 0);
        if (width > height) {
            ratio = (float) width/height;
            Matrix.orthoM(projectionMat, 0, -ratio, ratio, -1.0f, +1.0f, -1.0f, +1.0f);
        }
        else {
            ratio = (float) height/width;
            Matrix.orthoM(projectionMat, 0, -1.0f, +1.0f, -ratio, ratio, -1.0f, +1.0f);
        }
        Matrix.setIdentityM(modelviewMat, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        long now = System.currentTimeMillis();
        updateCoordFrames(now, now - lastMilliSec);
        lastMilliSec = now;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20
                .GL_DEPTH_BUFFER_BIT);

        Matrix.multiplyMM(tmpMat, 0, torus_cf, 0, tri_cf, 0);
        tri_one.draw(shColorArray, projectionMat, modelviewMat, tmpMat);
        tor_one.draw(shNoColor, projectionMat, modelviewMat, torus_cf);
    }
}
