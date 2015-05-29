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
    private Wheel w;
    private Arm a;
    private SwingFrame sf;
    private float[] wheel_cf, frame_cf, arm_cf;
    private float[] projectionMat, modelviewMat, normalMat;
    private float[] tmpMat1, tmpMat2;
    private long lastMilliSec;
    private Context context;
    private Shader shPhong;

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
        wheel_cf = new float[16];
        Matrix.setIdentityM(wheel_cf, 0);

        arm_cf = new float[16];
        Matrix.setIdentityM(arm_cf, 0);

        frame_cf = new float[16];
        Matrix.setIdentityM(frame_cf, 0);
        Matrix.translateM(frame_cf, 0, 0, 0, 20);

        setEGLContextClientVersion(2); /* Use OpenGL ES 2.0 */
        setRenderer(this);
        projectionMat = new float[16];
        modelviewMat = new float[16];
        normalMat = new float[16];
        tmpMat1 = new float[16];
        tmpMat2 = new float[16];
    }

    private void updateCoordFrames(long now, long delta)
    {
        Matrix.rotateM(wheel_cf, 0, 72.0f * delta / 1000.0f,
                0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.05f, 0.203f, 0.42f, 0.0f);
        w = new Wheel();
        a = new Arm();
        sf = new SwingFrame();
        shPhong = new Shader(context, "vs_phong.shdr",
                "fs_passthru.shdr");

        lastMilliSec = System.currentTimeMillis();
        Matrix.setIdentityM(wheel_cf, 0);
            /* The wheel axis is initially on the Z-axis, we should
             * turn it so it'll spin on the Y-axis */
        Matrix.rotateM(wheel_cf, 0, 90, 1, 0, 0);
            /* translate the wheel so its center is at the end of the
             * arm */
        Matrix.translateM(wheel_cf, 0, 0, -20, 0);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        float ratio;
        GLES20.glViewport(0, 0, width, height);
        Matrix.perspectiveM(projectionMat, 0, 60.0f,
                (float) width / height, 0.01f, 100.0f);

        Matrix.setLookAtM(modelviewMat, 0,
                    /* eye */ 25.0f, 20.0f, 20.0f,
                    /* center */ 0.0f, 0.0f, 10.0f,
                    /* up */ 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        long now = System.currentTimeMillis();
        updateCoordFrames(now, now - lastMilliSec);
        lastMilliSec = now;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20
                .GL_DEPTH_BUFFER_BIT);

        sf.draw(shPhong, projectionMat, modelviewMat, frame_cf);
        /* tmp1 = frame_cf * arm_cf */
        Matrix.multiplyMM(tmpMat1, 0, frame_cf, 0, arm_cf, 0);
        a.draw(shPhong, projectionMat, modelviewMat, tmpMat1);

        /* tmp2 = tmp1 * wheel_cf OR
         * tmp2 = frame_cf & arm_cf & wheel_cf */
        Matrix.multiplyMM(tmpMat2, 0, tmpMat1, 0, wheel_cf, 0);
        w.draw(shPhong, projectionMat, modelviewMat, tmpMat2);
    }
}
