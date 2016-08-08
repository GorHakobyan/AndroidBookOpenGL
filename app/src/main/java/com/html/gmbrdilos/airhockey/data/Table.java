package com.html.gmbrdilos.airhockey.data;

//     this class will store the position
//     data for our table, and we’ll also add texture coordinates to apply the texture
//     to the table.
public class Table {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
//     Order of coordinates: X, Y, S, T

//     Triangle Fan
            0f, 0f,          0.5f, 0.5f,
            -0.5f, -0.8f,    0f, 0.9f,
            0.5f, -0.8f,     1f, 0.9f,
            0.5f, 0.8f,      1f, 0.1f,
            -0.5f, 0.8f,     0f, 0.1f,
            -0.5f, -0.8f,    0f, 0.9f
    };
}
