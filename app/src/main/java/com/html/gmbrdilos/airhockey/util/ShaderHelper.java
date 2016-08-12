package com.html.gmbrdilos.airhockey.util;

//class that is going to create a new OpenGL shader object,
// compile our shader code, and return the shader object for that shader code.

import android.opengl.GLES20;
import android.util.Log;

public class ShaderHelper
{

    private static final String TAG = "ShaderHelper";

    /**
     * Loads and compiles a vertex shader, returning the OpenGL object ID.
     */
    public static int compileVertexShader(String shaderCode)
    {

        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);

    }

    /**
     * Loads and compiles a fragment shader, returning the OpenGL object ID.
     */
    public static int compileFragmentShader(String shaderCode)
    {

        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);

    }

    /**
     * Compiles a shader, returning the OpenGL object ID.
     */
    private static int compileShader(int type, String shaderCode)
    {

//        The first thing we should do is create a new shader object and check if the creation was successful.
//        We create a new shader object with a call to glCreateShader() and store the ID
//        of that object in shaderObjectId. The type can be GL_VERTEX_SHADER for a vertex
//        shader, or GL_FRAGMENT_SHADER for a fragment shader.


//        Create a new shader object.

        final int shaderObjectId = GLES20.glCreateShader(type);

        if (shaderObjectId == 0)
        {

            if (LoggerConfig.ON)
            {
                Log.w(TAG, "Could not create new shader.");
            }
            return 0;
        }

//        Pass in the shader source.
//        Once we have a valid shader object, we call glShaderSource(shaderObjectId, shaderCode)
//        to upload the source code. This call tells OpenGL to read in the source code
//        defined in the String shaderCode and associate it with the shader object referred to
//        by shaderObjectId.
        GLES20.glShaderSource(shaderObjectId, shaderCode);

//        Compile the shader.
//        This tells OpenGL to compile the source code that was previously uploaded
//        to shaderObjectId.
        GLES20.glCompileShader(shaderObjectId);

//        Get the compilation status.
//        To check whether the compile failed or succeeded, we first create a new int
//        array with a length of 1 and call it compileStatus. We then call glGetShaderiv(shaderObjectId,GLES20.GL_COMPILE_STATUS, compileStatus, 0).
//        This tells OpenGL to read the compile status associated with shaderObjectId and write it to the 0th element of
//        compileStatus.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (LoggerConfig.ON)
        {
//        Print the shader info log to the Android log output.
            Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:" + GLES20.glGetShaderInfoLog(shaderObjectId));
        }

//        Verify the compile status.
        if (compileStatus[0] == 0)
        {
//        If it failed, delete the shader object.
            GLES20.glDeleteShader(shaderObjectId);

            if (LoggerConfig.ON)
            {
                Log.w(TAG, "Compilation of shader failed.");
            }

            return 0;
        }

        // Return the shader object ID.
        return shaderObjectId;
    }

    /**
     * Links a vertex shader and a fragment shader together into an OpenGL
     * program. Returns the OpenGL program object ID, or 0 if linking failed.
     */

    public static int linkProgram(int vertexShaderId, int fragmentShaderId)
    {

//        Create a new program object with a call to glCreateProgram()
//        and store the ID of that object in programObjectId.

        final int programObjectId = GLES20.glCreateProgram();

        if (programObjectId == 0)
        {
            if (LoggerConfig.ON)
            {
                Log.w(TAG, "Could not create new program");
            }
            return 0;
        }

//        Attach the vertex shader to the program.
        GLES20.glAttachShader(programObjectId, vertexShaderId);

//        Attach the fragment shader to the program.
        GLES20.glAttachShader(programObjectId, fragmentShaderId);

//        We’re now ready to join our shaders together. We’ll do this with a call to glLinkProgram(programObjectId)
//        Link the two shaders together into a program.
        GLES20.glLinkProgram(programObjectId);

//        Get the link status.
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if (LoggerConfig.ON)
        {
//        Print the program info log to the Android log output.
            Log.v(TAG, "Results of linking program:\n" + GLES20.glGetProgramInfoLog(programObjectId));
        }

//        Verify the link status.
        if (linkStatus[0] == 0)
        {
//        If it failed, delete the program object.
            GLES20.glDeleteProgram(programObjectId);
            if (LoggerConfig.ON)
            {
                Log.w(TAG, "Linking of program failed.");
            }
            return 0;
        }

//        Return the program object ID.
        return programObjectId;
    }

    /**
     * Validates an OpenGL program. Should only be called when developing the
     * application.
     */

    public static boolean validateProgram(int programObjectId)
    {

        GLES20.glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog:" + GLES20.glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }

    //        This helper function will compile the shaders defined by vertexShaderSource and
//        fragmentShaderSource and link them together into a program. If logging is turned
//        on, it will also validate the program. We’ll use this helper function to build
//        up our base class.
    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource)
    {

        int program;

//     Compile the shaders.
        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

//     Link them into a shader program.
        program = linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON)
        {
            validateProgram(program);
        }

        return program;
    }
}
