package edu.gvsu.cis.dulimarh.gles_io_15;

/*
    This class loads vertex and fragment shaders from the Android assets
    directory.
 */
import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import java.io.IOException;
import java.util.Scanner;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_TRUE;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;

public class Shader {

    private final String TAG = getClass().getName();
    private Context context;
    private int vsId, fsId, progId;

    /* in a C program we would write the following call:
     *  glGetShaderiv (handle, GL_COMPILE_STATUS, &errorCode);
     *  
     *  Since Java does not provide direct pointers, we use
     *  an array of ONE integer to mimic a pointer to int.
     */
    private int[] errorCode;

    public Shader(Context context, String vsfile, String fsfile)
    {
        errorCode = new int[1];
        this.context = context;
        try {
            vsId = loadShaderCode(GL_VERTEX_SHADER, vsfile);
            fsId = loadShaderCode(GL_FRAGMENT_SHADER, fsfile);
            progId = glCreateProgram();
            if (progId != 0) {
                glAttachShader(progId, vsId);
                glAttachShader(progId, fsId);
                glLinkProgram(progId);
                glGetProgramiv(progId, GL_LINK_STATUS, errorCode, 0);
                if (errorCode[0] != GL_TRUE) {
                    glDeleteProgram(progId);
                    throw new IllegalStateException("Shader link error: " +
                            glGetProgramInfoLog(progId));
                }
            }
            else
                throw new IllegalStateException("Can't create a shader program");
        }
        catch (Exception e)
        {
           Log.e(TAG, "Caught exception: " + e.getMessage());
        }
    }

    private int loadShaderCode(int type, String fname)
    {
        int handle;
        try {
            /* Read the shader code from the Android assets folder */
            Scanner inp = new Scanner(context.getAssets().open(fname));
            StringBuffer code = new StringBuffer();
            while (inp.hasNextLine())
                code.append(inp.nextLine() + "\n");
            inp.close();
            handle = glCreateShader(type);
            if (handle != 0) {
                glShaderSource(handle, code.toString());
                glCompileShader(handle);
                
                glGetShaderiv(handle, GL_COMPILE_STATUS, errorCode, 0);
                if (errorCode[0] != 0)
                    return handle;
                else {
                    Log.e("Shader", "Shader compile error: " +
                            glGetShaderInfoLog(handle));
                    glDeleteShader(handle);
                    throw new IllegalStateException("Shader compile error in " + fname);
                }
            }
            else {
                throw new IllegalStateException("Failed to create a shader for " + fname);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Shader I/O exception while loading " + fname + ": "
                            + e.getMessage());
        }
    }
    
    public int getId()
    {
        return progId;
    }
}
