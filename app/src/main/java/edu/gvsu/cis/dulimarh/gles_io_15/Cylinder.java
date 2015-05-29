package edu.gvsu.cis.dulimarh.gles_io_15;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static android.opengl.GLES10.GL_FLOAT;

/**
 * Created by dulimarh on 4/6/15.
 */
public class Cylinder {
    private final int N_POINTS = 20;
    private FloatBuffer vBuf, nBuf;
    private IntBuffer iBuf;
    private float[] tmp;
//    private Shader shader;
    private Material mat;
//    private Map<String,Buffer> bMap;
//    private Map<String,float[]> uniformMap;

    public Cylinder(Material mat, float topRad, float botRad,
                    float height)
    {
//        shader = sh;
        this.mat = mat;
        double delta = 2 * Math.PI / N_POINTS;
//        bMap = new HashMap<String, Buffer>();
//        uniformMap = new HashMap<String,float[]>();
        ByteBuffer buff;
        /* 3 values per vertex, 4 types per value */
        buff = ByteBuffer.allocateDirect(3 * (2 * N_POINTS + 1) * 4);
        buff.order(ByteOrder.nativeOrder());
        vBuf = buff.asFloatBuffer();

        buff = ByteBuffer.allocateDirect(3 * (2 * N_POINTS + 1) * 4);
        buff.order(ByteOrder.nativeOrder());
        nBuf = buff.asFloatBuffer();
        /* points in top ring */

        float angle = 0.0f;
        for (int k = 0; k  < N_POINTS; k++) {
            double x = topRad * Math.cos(angle);
            double y = topRad * Math.sin(angle);
            vBuf.put((float) x);
            vBuf.put((float) y);
            vBuf.put(height / 2);
            angle += delta;
        }

        /* vertices at the bottom ring */
        angle = 0.0f;
        for (int k = 0; k  < N_POINTS; k++)
        {
            double x = botRad * Math.cos(angle);
            double y = botRad * Math.sin(angle);
            vBuf.put ((float) x);
            vBuf.put ((float) y);
            vBuf.put (-height/2);
            angle += delta;
        }

    /* push the TOP CENTER point */
        vBuf.put (0);  /* x */
        vBuf.put (0); /* y */
        vBuf.put (height/2); /* z */


        for (int n = 0; n < 2; n++) {
            angle = 0;
            for (int k = 0; k < N_POINTS; k++) {
                double xTop = topRad * Math.cos(angle);
                double yTop = topRad * Math.sin(angle);
                double xBot = botRad * Math.cos(angle);
                double yBot = botRad * Math.sin(angle);
                double[] vertTangent = {xTop - xBot, yTop - yBot, height};
                double[] horTangent ={Math.sin(angle), -Math.cos(angle),
                        0};
                double[] normal = MathUtils.cross
                        (vertTangent,
                                horTangent);
                MathUtils.normalize(normal);
                nBuf.put((float) normal[0]);
                nBuf.put((float) normal[1]);
                nBuf.put((float) normal[2]);
                angle += delta;
            }
        }
        nBuf.put(0); /* normal vector for the top center vertice */
        nBuf.put(0);
        nBuf.put(1.0f);

        /* 2 values per point, 4 bytes per value */
        buff = ByteBuffer.allocateDirect(2 * (3*N_POINTS+4) * 4);
        buff.order(ByteOrder.nativeOrder());
        iBuf = buff.asIntBuffer();
        /* fill in the vertices */
        for (int k = 0; k < N_POINTS; k++)
        {
            iBuf.put (k);
            iBuf.put (k + N_POINTS);
        }
        /* close the quad_strip_index for wall */
        iBuf.put (0);
        iBuf.put (N_POINTS);

        /* first index of the tri-fan */
        iBuf.put (N_POINTS * 2);
        /* index of rest of the tri-fan */
        for (int k = 0; k < N_POINTS; k++)
            iBuf.put (k);
        /* close the last arc of the tri-fan */
        iBuf.put (0);

        vBuf.rewind();
        nBuf.rewind();
        iBuf.rewind();
//        bMap.put("a_position", vBuf);
//        bMap.put("a_normal", nBuf);
//        if (this.mat != null) {
//            uniformMap.put("u_materialAmbient", this.mat.ambient);
//            uniformMap.put("u_materialDiffuse", this.mat.diffuse);
//            uniformMap.put("u_materialSpecular", this.mat.specular);
//        }
        tmp = new float[16];
    }

    public void draw(Shader sh, float[] projMat, float[] mvMat, float[]
            coordFrame) {
        GLES20.glUseProgram(sh.getId());
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

        handle = GLES20.glGetUniformLocation(shaderId, "u_matDiffuse");
        GLES20.glUniform4fv(handle, 1, mat.diffuse, 0);

        handle = GLES20.glGetAttribLocation(shaderId, "a_pos");
        GLES20.glEnableVertexAttribArray(handle);

        GLES20.glVertexAttribPointer(handle, 3, GL_FLOAT, false, 0, vBuf);


        iBuf.rewind();
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 2 * N_POINTS + 2,
                GLES20.GL_UNSIGNED_INT, iBuf);
        iBuf.position(2*N_POINTS + 2);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, N_POINTS + 2,
                GLES20.GL_UNSIGNED_INT, iBuf);
    }
}
