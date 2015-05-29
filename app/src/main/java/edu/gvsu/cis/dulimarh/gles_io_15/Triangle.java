package edu.gvsu.cis.dulimarh.gles_io_15;

import android.opengl.GLES20;
import android.opengl.Matrix;

import static android.opengl.GLES10.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Triangle {

    private final static int NUM_VERTICES = 3;
    
    private FloatBuffer mVertex, mColor;
    private ShortBuffer mIndex;
    private float[] tmp;
    /* The order of vertices given below DOES NOT have to be in clockwise
     * nor counter clockwise order. But the order of the indices MUST */
    
    /* our ortographic projection parameters guarantee that the shorter
     * spans [-1.0, 1.0] 
     */
    /* equilateral triangle with unit side and COG at (0.0) */
    private float[] coords = {
            -0.5f, -(float) Math.sqrt(3)/6, 0.0f,  /* lower left */
            +0.5f, -(float) Math.sqrt(3)/6, 0.0f,  /* lower right */
             0.0f,  (float) Math.sqrt(3)/3, 0.0f}; /* top */
    
    private float[] colors = {
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f
    };
    
    /* the indices must be specified in the correct order so the triangle
     * vertices are rendered in CCW order */
    private short[] order = {0, 1, 2};
    
    public Triangle()
    {
        /* Allocate buffers from the heap so they won't get wiped out
         * by Java Garbage Collector.
         */
        
        /* Each vertex stores floating-point data (4 bytes) for (x,y,z) */
        ByteBuffer vBuff = ByteBuffer.allocateDirect(coords.length * 4);
        vBuff.order(ByteOrder.nativeOrder());
        mVertex = vBuff.asFloatBuffer();

        ByteBuffer cBuff = ByteBuffer.allocateDirect(colors.length * 4);
        cBuff.order(ByteOrder.nativeOrder());
        mColor = cBuff.asFloatBuffer();
        
        /* each index is a short integer value (2 bytes) */
        ByteBuffer iBuff = ByteBuffer.allocateDirect(NUM_VERTICES * 2);
        iBuff.order(ByteOrder.nativeOrder());
        mIndex = iBuff.asShortBuffer();
        
        /* place the coordinates and indices into the corresponding buffers */
        for (float val : coords)
            mVertex.put(val);
        for (float col : colors)
            mColor.put(col);
        
        /* the order of vertices is already CCW */
        for (short k : order)
            mIndex.put(k);
        
        /* reset the buffer position to the beginning */
        mVertex.position(0);
        mColor.position(0);
        mIndex.position(0);

        tmp = new float[16];
    }
    
    public void draw(Shader sh, float[] projMat, float[] mvMat, float[]
            coordFrame)
    {
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
        GLES20.glVertexAttribPointer(handle,
                3           /* number of coordinates per vertex */,
                GL_FLOAT    /* type of each vertex */,
                false,
                0           /* stride */,
                mVertex     /* memory location */);
        handle = GLES20.glGetAttribLocation(shaderId, "a_col");
        GLES20.glEnableVertexAttribArray(handle);
        GLES20.glVertexAttribPointer(handle,
                4           /* number of coordinates per vertex */,
                GL_FLOAT    /* type of each vertex */,
                false,
                0           /* stride */,
                mColor     /* memory location */);
        GLES20.glDrawElements(
                GL_TRIANGLES, /* mode */
                order.length       /* number of elements */,
                GL_UNSIGNED_SHORT  /* type of each index */,
                mIndex);
    }
}
