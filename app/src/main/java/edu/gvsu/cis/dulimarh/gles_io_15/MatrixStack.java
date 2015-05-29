package edu.gvsu.cis.dulimarh.gles_io_15;

import java.util.Stack;

public class MatrixStack {
    private Stack<Float> stack;
    //private float[] temp;

    public MatrixStack()
    {
        stack = new Stack<Float>();
    }

    public void push(float[] m) {
        //temp = new float[m.length];
        //System.arraycopy(m, 0, temp, 0, m.length);
        for (int k = 0; k < m.length; k++)
            stack.push(m[k]);
    }
    
    public void pop(float[] m)
    {
        for (int k = m.length - 1; k >= 0; k--)
            m[k] = stack.pop();
        /* copy the saved array, element by element */
        //System.arraycopy(temp, 0, m, 0, m.length);
    }
}
