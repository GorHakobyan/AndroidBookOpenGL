package com.html.gmbrdilos.airhockey.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.html.gmbrdilos.airhockey.R;

public class TextureShaderProgram {

//     Uniform locations
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

//     Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader,
                R.raw.texture_fragment_shader);
//     Retrieve uniform locations for the shader program.
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);

//     Retrieve attribute locations for the shader program.
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }
}
