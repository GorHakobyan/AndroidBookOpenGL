package com.html.gmbrdilos.airhockey.data;


import android.opengl.GLES20;

import com.html.gmbrdilos.airhockey.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class VertexArray
{

//     Will be used to store our vertex array data
    private final FloatBuffer floatBuffer;

//     The constructor takes in an array of Java floating-point
//     data and writes it to the buffer.
    public VertexArray(float[] vertexData)
    {

        floatBuffer = ByteBuffer
//        We allocated a block of native memory
//        using ByteBuffer.allocateDirect(); this memory will not be managed by the garbage
//        collector. We need to tell the method how large the block of memory should
//        be in bytes. Since our vertices are stored in an array of floats and there are
//        4 bytes per float, we pass in tableVerticesWithTriangles.length * BYTES_PER_FLOAT.

                .allocateDirect(vertexData.length * Constants.BYTES_PER_FLOAT)

//        The next line tells the byte buffer that it should organize its bytes in native
//        order. When it comes to values that span multiple bytes, such as 32-bit
//        integers, the bytes can be ordered either from most significant to least significant
//        or from least to most. Think of this as similar to writing a number either
//        from left to right or right to left. It’s not important for us to know what that
//        order is, but it is important that we use the same order as the platform. We
//        do this by calling order(ByteOrder.nativeOrder())

                .order(ByteOrder.nativeOrder())

//        Finally, we’d rather not deal with individual bytes directly. We want to work
//        with floats, so we call asFloatBuffer() to get a FloatBuffer that reflects the underlying
//        bytes. We then copy data from Dalvik’s memory to native memory by calling
//        vertexData.put(tableVerticesWithTriangles). The memory will be freed when the process
//        gets destroyed, so we don’t normally need to worry about that.

                .asFloatBuffer()

                .put(vertexData);
    }

//    A generic method to associate an attribute in our shader with the data.
    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride)
    {

        floatBuffer.position(dataOffset);

        GLES20.glVertexAttribPointer(

//                int index: This is the attribute location, and we pass in aPositionLocation to refer to the location that we retrieved earlier
                attributeLocation,

//                int size: This is the data count per attribute, or how many components are associated with each vertex for this attribute.
                componentCount,

//                int type: This is the type of data.
                GLES20.GL_FLOAT,

//                boolean normalized: This only applies if we use integer data
                false,

//                int stride: applies when we store more than one attribute in a single array.
                stride,

//                Buffer ptr: This tells OpenGL where to read the data. Don’t forget that it will start reading from the buffer’s current position,
//                so if we hadn’t called vertexData.position(...), it would probably
//                try to read past the end of the buffer and crash our application.
                floatBuffer
        );

//        After calling glVertexAttribPointer(), OpenGL now knows where to read the data
//        for the attribute.

//        Now that we’ve linked our data to the attribute, we need to enable the attribute
//        with a call to glEnableVertexAttribArray() before we can start drawing.
//        With this final call, OpenGL now knows where to find all the data it needs.
        GLES20.glEnableVertexAttribArray(attributeLocation);

        floatBuffer.position(0);
    }
}
