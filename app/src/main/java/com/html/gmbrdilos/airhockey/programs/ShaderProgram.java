package com.html.gmbrdilos.airhockey.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.html.gmbrdilos.airhockey.util.ShaderHelper;
import com.html.gmbrdilos.airhockey.util.TextResourceReader;

public class ShaderProgram
{

//     Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_COLOR = "u_Color";

//     Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

//     Shader program
    protected final int program;

//    In the constructor,we call the helper function that we’ve just defined, and we use it to build an
//    OpenGL shader program with the specified shaders.
    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId)
    {
//     Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId)
        );
    }

    public void useProgram()
    {
//      Set the current OpenGL shader program to this program.
        GLES20.glUseProgram(program);
    }
}
