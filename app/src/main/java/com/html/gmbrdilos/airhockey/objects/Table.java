package com.html.gmbrdilos.airhockey.objects;

import android.opengl.GLES20;

import com.html.gmbrdilos.airhockey.Constants;
import com.html.gmbrdilos.airhockey.data.VertexArray;
import com.html.gmbrdilos.airhockey.programs.TextureShaderProgram;

//     this class will store the position
//     data for our table, and we’ll also add texture coordinates to apply the texture
//     to the table.
public class Table
{

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
//     Order of coordinates: X, Y, S, T

//     Triangle Fan

//     The component T runs in opposite direction of component
//     max of Y is 0.8, min of Y is -0.8
//     max of T is 0.9, min of T is 0.1

//     We also used T coordinates of 0.1f and 0.9f. Why? Well, our table is 1 unit
//     wide and 1.6 units tall. Our texture image is 512 x 1024 in pixels, so if the
//     width corresponds to 1 unit, the texture is actually 2 units tall. To avoid
//     squashing the texture, we use the range 0.1 to 0.9 instead of 0.0 to 1.0 to
//     clip the edges and just draw the center portion.
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1f, 0.9f,
            0.5f, 0.8f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.9f
    };

    private final VertexArray vertexArray;

//     This constructor will use VertexArray
//     to copy the data over into a FloatBuffer in native memory.
    public Table()
    {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

//     A method to bind the vertex array to a shader program:
    public void bindData(TextureShaderProgram textureProgram)
    {

//        setVertexAttribPointer() called for each attribute, getting the location of each attribute from the shader program.
//        This will bind the position data to the shader attribute referenced by getPositionAttributeLocation() and bind
//        the texture coordinate data to the shader attribute referenced by getTextureCoordinatesAttributeLocation().

        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw()
    {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
    }
}
