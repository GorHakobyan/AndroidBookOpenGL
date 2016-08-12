package com.html.gmbrdilos.airhockey.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.html.gmbrdilos.airhockey.R;

public class ColorShaderProgram extends ShaderProgram
{

//     Uniform locations
    private final int uMatrixLocation;

//     Attribute locations
    private final int aPositionLocation;
    private final int uColorLocation;
//    private final int aColorLocation;

    public ColorShaderProgram(Context context)
    {

        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);

//     Retrieve uniform locations for the shader program.
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

//     Retrieve attribute locations for the shader program.
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);

        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
//        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
    }

//    public void setUniforms(float[] matrix) {
////     Pass the matrix into the shader program.
//        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
//    }

    public void setUniforms(float[] matrix, float r, float g, float b)
    {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        GLES20.glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public int getPositionAttributeLocation()
    {
        return aPositionLocation;
    }

//    public int getColorAttributeLocation() {
//        return aColorLocation;
//    }
}
