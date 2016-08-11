package com.html.gmbrdilos.airhockey.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.html.gmbrdilos.airhockey.R;

public class TextureShaderProgram extends ShaderProgram {

//     Uniform locations
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

//    For 2nd texture (BLENDING EXERCISE)
    private final int uTextureUnitLocation1;

//     Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

//     Constructor to initialize the shader program
    public TextureShaderProgram(Context context) {

//        This constructor will call the superclass with our selected resources, and the
//        superclass will build the shader program. We’ll then read in and save the
//        uniform and attribute locations.

        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

//        Retrieve uniform locations for the shader program.
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);

//        For 2nd texture (BLENDING EXERCISE)
        uTextureUnitLocation1 = GLES20.glGetUniformLocation(program, "u_TextureUnit1");

//        Retrieve attribute locations for the shader program.
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix, int textureId) {
//        Pass the matrix into the shader program.
        GLES20.glUniformMatrix4fv(

//               Location
                uMatrixLocation,

//               Count
                1,

//               Transpose
                false,

//               Value
                matrix,

//               Offset
                0
        );

//        When we draw using textures
//        in OpenGL, we don’t pass the texture directly in to the shader. Instead, we
//        use a texture unit to hold the texture. We do this because a GPU can only
//        draw so many textures at the same time. It uses these texture units to represent
//        the active textures currently being drawn.
//        We can swap textures in and out of texture units if we need to switch textures,
//        though this may slow down rendering if we do it too often. We can also use
//        several texture units to draw more than one texture at the same time.
//        We start out this part by setting the active texture unit to texture unit 0 with a
//        call to glActiveTexture(), and then we bind our texture to this unit with a call
//        to glBindTexture(). We then pass in the selected texture unit to u_TextureUnit in the
//        fragment shader by calling glUniform1i(uTextureUnitLocation, 0).

//        Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

//        Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

//        Tell the texture uniform sampler to use this texture in the shader by
//        telling it to read from texture unit 0.
        GLES20.glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }

//     For 2nd texture (BLENDING EXERCISE)
    public int getuMatrixLocation() {
        return uMatrixLocation;
    }

    public int getuTextureUnitLocation(){
        return uTextureUnitLocation1;
    }
}
