package edu.gvsu.cis.dulimarh.gles_io_15;

import android.opengl.Matrix;

/**
 * Created by dulimarh on 4/6/15.
 */
public class Arm {
    private static final float LENGTH = 20.0f;
    private Cylinder c;
    private MatrixStack mvStack;

    public Arm()
    {
        Material mat = new Material();
        mat.ambient = new float[]{0.02f, 0.02f, 0.02f, 1.0f};
        mat.diffuse = new float[]{0.34f, 0.612f, 0.03f, 1.0f};
        mat.specular = new float[]{0.74f, 0.612f, 0.63f, 1.0f};
        c = new Cylinder(mat, 0.2f, 0.2f, LENGTH);
        mvStack = new MatrixStack();
    }

    public void draw (Shader sh, float[] projMat, float[] mvMat, float[]
                      objCF) {
        mvStack.push(objCF);
        Matrix.translateM(objCF, 0, 0, 0, -LENGTH/2);
        mvStack.push(objCF);
        Matrix.translateM(objCF, 0, 0, -0.5f, 0);
        c.draw(sh, projMat, mvMat, objCF);
        mvStack.pop(objCF);
        mvStack.push(objCF);
        Matrix.translateM(objCF, 0, 0, +0.5f, 0);
        c.draw(sh, projMat, mvMat, objCF);
        mvStack.pop(objCF);
        mvStack.pop(objCF);
    }
}
