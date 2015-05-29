package edu.gvsu.cis.dulimarh.gles_io_15;

import android.opengl.Matrix;

/**
 * Created by dulimarh on 4/6/15.
 */
public class Wheel {
    private final static float RADIUS = 3.5f;
    private final static float TIRE_THICKNESS = 0.4f;
    private final static int NUM_SPOKES = 5;

    private Torus t;
    private Cylinder c;
    private MatrixStack mvStack;
    private float[] tmp;

    public Wheel()
    {
        mvStack = new MatrixStack();
        tmp = new float[16];
        Material rubberMat = new Material();
        rubberMat.ambient = new float [] {0.02f, 0.02f, 0.02f, 1.0f};
        rubberMat.diffuse = new float [] {0.01f, 0.01f, 0.01f, 1.0f};
        rubberMat.specular = new float [] {0.4f, 0.4f, 0.4f, 1.0f};
        t = new Torus(rubberMat, RADIUS, TIRE_THICKNESS, 30, 15, 360);

        Material chromeMat = new Material();
        chromeMat.ambient = new float[] {0.02f, 0.02f, 0.02f, 1.0f};
        chromeMat.diffuse = new float[] {0.01f, 0.01f, 0.01f, 1.0f};
        chromeMat.specular = new float[] {0.4f, 0.4f, 0.4f, 1.0f};
        c = new Cylinder(chromeMat, 0.75f * TIRE_THICKNESS,
                0.75f * TIRE_THICKNESS, RADIUS);

    }

    public void draw (Shader sh, float[] projMat, float[] mvMat, float[]
            objCoordFrame)
    {
        t.draw(sh, projMat, mvMat, objCoordFrame);

        System.arraycopy(objCoordFrame, 0, tmp, 0, objCoordFrame.length);
        Matrix.rotateM(tmp, 0, 90, 1, 0, 0);
        for (int k = 0; k < NUM_SPOKES; k++) {
            float angle = k * 360.0f/NUM_SPOKES;
            mvStack.push(tmp);
            Matrix.rotateM(tmp, 0, angle, 0, 1, 0);
            Matrix.translateM(tmp, 0, 0, 0, RADIUS/2);
            c.draw(sh, projMat, mvMat, tmp);
            mvStack.pop(tmp);
        }

    }
}
