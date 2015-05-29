package edu.gvsu.cis.dulimarh.gles_io_15;

import android.opengl.Matrix;

/**
 * Created by dulimarh on 4/1/15.
 */
public class MathUtils {
    public static double[] cross (double[] a, double[] b)
    {
        double[] result = new double[3];
        result[0] = a[1]*b[2] - a[2]*b[1];
        result[1] = a[2]*b[0] - a[0]*b[2];
        result[2] = a[0]*b[1] - a[1]*b[0];
        return result;
    }

    public static float[] cross (float[] a, float[] b)
    {
        float[] result = new float[3];
        result[0] = a[1]*b[2] - a[2]*b[1];
        result[1] = a[2]*b[0] - a[0]*b[2];
        result[2] = a[0]*b[1] - a[1]*b[0];
        return result;
    }

    public static float angle(float[] a, float[] b) {
        float numer = a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
        return (float) Math.acos(numer / (Matrix.length(a[0],
                a[1], a[2]) * Matrix.length(b[0], b[1], b[2])));

    }

    public static void normalize(double[] a)
    {
        //double[] result = new double[3];
        double len = Matrix.length((float)a[0], (float) a[1],
                (float) a[2]);
        a[0] = a[0]/len;
        a[1] = a[1]/len;
        a[2] = a[2]/len;
        //return result;
    }

    public static void normalize(float[] a)
    {
        //float[] result = new float[3];
        final float len = length(a);
        a[0] = a[0]/len;
        a[1] = a[1]/len;
        a[2] = a[2]/len;
        //return result;
    }

    public static float length(float[] a) {
        return (float) Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] *
                a[2]);
    }

    public static void rotationMatrixToQuaternion (float[] R, float[] q)
    {
        final float m00 = R[0];
        final float m10 = R[1];
        final float m20 = R[2];
        final float m01 = R[4];
        final float m11 = R[5];
        final float m21 = R[6];
        final float m02 = R[8];
        final float m12 = R[9];
        final float m22 = R[10];
        
        float tr = m00 + m11 + m22;

        if (tr > 0) {
            float S = (float) Math.sqrt(tr+1.0) * 2; // S=4*qw 
            q[0] = 0.25f * S;
            q[1] = (m21 - m12) / S;
            q[2] = (m02 - m20) / S;
            q[3] = (m10 - m01) / S;
        } else if ((m00 > m11)&(m00 > m22)) {
            float S = (float) Math.sqrt(1.0 + m00 - m11 - m22) * 2; //
            // S=4*q[1]
            q[0] = (m21 - m12) / S;
            q[1] = 0.25f * S;
            q[2] = (m01 + m10) / S;
            q[3] = (m02 + m20) / S;
        } else if (m11 > m22) {
            float S = (float) Math.sqrt(1.0 + m11 - m00 - m22) * 2; //
            // S=4*q[2]
            q[0] = (m02 - m20) / S;
            q[1] = (m01 + m10) / S;
            q[2] = 0.25f * S;
            q[3] = (m12 + m21) / S;
        } else {
            float S = (float) Math.sqrt(1.0 + m22 - m00 - m11) * 2; //
            // S=4*q[3]
            q[0] = (m10 - m01) / S;
            q[1] = (m02 + m20) / S;
            q[2] = (m12 + m21) / S;
            q[3] = 0.25f * S;
        }

    }
}
