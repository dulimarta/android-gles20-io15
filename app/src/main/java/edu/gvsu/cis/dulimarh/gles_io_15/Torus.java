package edu.gvsu.cis.dulimarh.gles_io_15;


import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES10.*;

/**
 * Created by dulimarh on 4/1/15.
 */
public class Torus  {
    private int majorRings, minorRings;
    private boolean fullTorus;
    private int[] buffers;
    private FloatBuffer vBuf, nBuf;
    private ShortBuffer[] iBuf;
    private float[] tmp;

    public Torus(float RAD, float rad, int majorRings,
                 int minorRings,
                 int span) {
        final int N = majorRings * minorRings;

        ByteBuffer buff;
        /* each vertex has 3 values (x,y,z) and each value takes 4 bytes */
        buff = ByteBuffer.allocateDirect(3 * N * 4);
        buff.order(ByteOrder.nativeOrder());
        vBuf = buff.asFloatBuffer();

        buff = ByteBuffer.allocateDirect(3 * N * 4);
        buff.order (ByteOrder.nativeOrder());
        nBuf = buff.asFloatBuffer();

        iBuf = new ShortBuffer[minorRings];
        for (int k = 0; k  < minorRings; k++)
        {
            buff = ByteBuffer.allocateDirect(2 * majorRings * 4);
            buff.order(ByteOrder.nativeOrder());
            iBuf[k] = buff.asShortBuffer();
        }

        this.majorRings = majorRings;
        this.minorRings = minorRings;
        fullTorus = span == 360;

        double dMinor = 2 * Math.PI / (minorRings - 1);
        double dMajor = (float) Math.toRadians((float) span) / (majorRings -
                1);
        float minAngle = 0;
        for (int k = 0; k < minorRings; k++) {
            float z = (float) (rad * Math.sin(minAngle));
            float majAngle = 0;
            for (int m = 0; m < majorRings; m++) {
                double R = RAD + rad * Math.cos(minAngle);
                vBuf.put((float) (R * Math.cos(majAngle)));
                vBuf.put((float) (R * Math.sin(majAngle)));
                vBuf.put(z);
            /* calculate normal */

                double[] majTangent = {-Math.sin(majAngle),
                        Math.cos(majAngle), 0};
                double[] minTangent = {
                    -Math.cos(majAngle) * Math.sin(minAngle),
                            -Math.sin(majAngle) * Math.sin(minAngle),
                            Math.cos(minAngle)};
                double[] n = MathUtils.cross(majTangent, minTangent);
                MathUtils.normalize(n);
                nBuf.put((float)n[0]);
                nBuf.put((float)n[1]);
                nBuf.put((float)n[2]);
                majAngle += dMajor;
            };
        /* index to go back to the first vertex on this ring */
            minAngle += dMinor;
        }

    /* generate the indices of the quad strips */
        int ringStart = 0;
        for (int k = 0; k < minorRings; k++) {
            for (int m = 0; m < majorRings; m++) {
                iBuf[k].put((short)((ringStart + m + majorRings) % N));
                iBuf[k].put((short)(ringStart + m));
            }
            ringStart += majorRings;
        }

        //buffers = new int[3]; /* vertex, normal, index */

        /* because put() alters the buffers current position,
           we have to rewind them
         */
        vBuf.rewind();
        nBuf.rewind();
        for (ShortBuffer ib : iBuf)
            ib.rewind();
        tmp = new float[16];
    }


    public void draw(Shader sh, float[] projMat, float[] mvMat, float[]
            coordFrame) {
        final int shaderId = sh.getId();
        GLES20.glUseProgram(shaderId);
        int handle;
        handle = GLES20.glGetUniformLocation(shaderId,
                "u_projectionMatrix");
        GLES20.glUniformMatrix4fv(handle,
                1, /* count */
                false, /* matrix is NOT transposed */
                projMat,
                0); /* offset */
        handle = GLES20.glGetUniformLocation(shaderId,
                "u_modelViewMatrix");
        Matrix.multiplyMM(tmp, 0, mvMat, 0, coordFrame, 0);
        GLES20.glUniformMatrix4fv(handle, 1, false, tmp, 0);

        handle = GLES20.glGetAttribLocation(shaderId, "a_pos");
        GLES20.glEnableVertexAttribArray(handle);

        GLES20.glVertexAttribPointer(handle, 3, GL_FLOAT, false, 0, vBuf);

        final int N = 2 * majorRings; // + (fullTorus ? 2 : 0);
        for (int k = 0; k < minorRings; k++) {
            /* each triangle strip must be rendered using a separate call */
            GLES20.glDrawElements(GL_TRIANGLE_STRIP, N, GL_UNSIGNED_SHORT,
                    iBuf[k]);
        }
    }
}
