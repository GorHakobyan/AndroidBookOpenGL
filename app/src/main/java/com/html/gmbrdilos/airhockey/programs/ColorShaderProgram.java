package com.html.gmbrdilos.airhockey.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.html.gmbrdilos.airhockey.R;

public class ColorShaderProgram extends ShaderProgram {

//     Uniform locations
    private final int uMatrixLocation;
//     Attribute locations

    private final int aPositionLocation;
    private final int aColorLocation;

    public ColorShaderProgram(Context context) {

        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);

//     Retrieve uniform locations for the shader program.
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

//     Retrieve attribute locations for the shader program.
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
    }
}
