package edu.gvsu.cis.dulimarh.gles_io_15;

import android.opengl.Matrix;

/**
 * Created by dulimarh on 4/7/15.
 */
public class SwingFrame {
    private static final float ELBOW_RADIUS = 2.0f;
    private static final float PIPE_RADIUS = 0.5f;
    private Cylinder pipe;
    private Torus elbow;
    private Material mat;
    private MatrixStack cf_stack;
    float[] current = new float[16];

    public SwingFrame() {
        cf_stack = new MatrixStack();
        mat = new Material();
        mat.ambient = new float[]  {0.191250f, 0.073500f, 0.022500f,
                1.0f};
        mat.diffuse = new float[]  {0.703800f, 0.270480f, 0.082800f,
                1.0f};
        mat.specular = new float[]  {0.256777f, 0.137622f, 0.086014f,
                1.0f};
        pipe = new Cylinder(mat, PIPE_RADIUS, PIPE_RADIUS, 1.0f);
        elbow = new Torus(mat, ELBOW_RADIUS, PIPE_RADIUS, 10, 20, 90);
    }

    public void draw (Shader sh, float[] projMat, float[] mvMat, float[]
            objCoordFrame)
    {
        System.arraycopy(objCoordFrame, 0, current, 0, 16);
        cf_stack.push(current);
        Matrix.rotateM(current, 0, 90, 1, 0, 0);
        Matrix.scaleM(current, 0, 1, 1, 20);
        pipe.draw(sh, projMat, mvMat, current);
        cf_stack.pop(current);

        /* right elbow */
        cf_stack.push(current);
        Matrix.rotateM(current, 0, 90, 0, 1, 0);
        Matrix.rotateM(current, 0, 90, 0, 0, 1);
        Matrix.translateM (current, 0, 10, -2, 0);
        elbow.draw(sh, projMat, mvMat, current);
        cf_stack.pop(current);

        /* left elbow */
        cf_stack.push(current);
        Matrix.rotateM(current, 0, 90, 0, 1, 0);
        Matrix.rotateM(current, 0, 180, 0, 0, 1);
        Matrix.translateM (current, 0, -2, 10, 0);
        elbow.draw(sh, projMat, mvMat, current);
        cf_stack.pop(current);

        /* left support */
        cf_stack.push(current);
        Matrix.translateM (current, 0, 0, -12.0f, -13.5f);
        Matrix.scaleM(current, 0, 1, 1, 23);
        pipe.draw(sh, projMat, mvMat, current);
        cf_stack.pop(current);

        /* right support */
        cf_stack.push(current);
        Matrix.translateM (current, 0, 0, 12.0f, -13.5f);
        Matrix.scaleM(current, 0, 1, 1, 23);
        pipe.draw(sh, projMat, mvMat, current);
        cf_stack.pop(current);

    }
}
