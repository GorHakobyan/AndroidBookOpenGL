package com.html.gmbrdilos.airhockey.objects;


public class ObjectBuilder {

//    A constant to represent how many floats we need for a vertex
    private static final int FLOATS_PER_VERTEX = 3;

//    An array to hold these vertices
    private final float[] vertexData;

//    A variable to keep track of the position in the array for the next vertex
    private int offset = 0;

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }
}
