package com.html.gmbrdilos.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.html.gmbrdilos.airhockey.util.LoggerConfig;
import com.html.gmbrdilos.airhockey.util.MatrixHelper;
import com.html.gmbrdilos.airhockey.util.ShaderHelper;
import com.html.gmbrdilos.airhockey.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyRenderer implements GLSurfaceView.Renderer{

//    As we have 2D object, there is only x and y: 2 components per vertex
    private static final int POSITION_COMPONENT_COUNT = 2;

//    X,Y,Z,W
//    private static final int POSITION_COMPONENT_COUNT = 4;

//    A float in Java has
//    32 bits of precision, while a byte has 8 bits of precision. This might seem like
//    an obvious point to make, but there are 4 bytes in every float.
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String A_COLOR = "a_Color";

//    RGB
    private static final int COLOR_COMPONENT_COUNT = 3;

//    This holds the name of the new uniform that we defined in our vertex shader.
    private static final String U_MATRIX = "u_Matrix";

//    We’ll also need a floating point array to store the matrix:
//    The destination array — this array’s length should be at least sixteen elements so
//    it can store the orthographic projection matrix.
    private final float[] projectionMatrix = new float[16];

//    TRANSLATION Matrix used to move obect.
//    We’ll use this matrix to move the air hockey table into the distance.
    private final float[] modelMatrix = new float[16];

//   We’ll also need an integer to hold the location of the matrix uniform:
    private int uMatrixLocation;

//    As we now have both a position and a color attribute in the same data array, OpenGL
//    can no longer assume that the next position follows immediately after the
//    previous position. Once OpenGL has read the position for a vertex, it will have
//    to skip over the color for the current vertex if it wants to read the position for
//    the next vertex. We’ll use the stride to tell OpenGL how many bytes are
//    between each position so that it knows how far it has to skip.

    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private int aColorLocation;

    private final Context context;

//    This integer will store the ID of the linked program.
    private int program;

//    We’ve created a constant for the name of our uniform and a variable to hold
//    its location in the OpenGL program object. Uniform locations don’t get specified
//    beforehand, so we’ll need to query the location once the program’s been successfully
//    linked. A uniform’s location is unique to a program object: even if
//    we had the same uniform name in two different programs, that doesn’t mean
//    that they’ll share the same location.
//
//    private static final String U_COLOR = "u_Color";
//    private int uColorLocation;

    public AirHockeyRenderer(Context context) {

        this.context = context;

        float[] tableVerticesWithTriangles = {
//               Order of coordinates: X, Y, Z, W, R, G, B

//               Triangle Fan
//               The first question you might be asking is, “Why did we only define six points?
//               Don’t we need to define three vertices per triangle?” While it’s true that we
//               need three vertices per triangle, we can sometimes reuse the same vertex in
//               more than one triangle.
//               A triangle fan begins with a center vertex, using the next two vertices to create
//               the first triangle. Each subsequent vertex will create another triangle, fanning
//               around the original center point. To complete the fan, we just repeat the
//               second point at the end.

//                0f, 0f, 0f, 1.5f, 1f, 1f, 1f,
//                -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
//                0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
//                0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
//                -0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
//                -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,

//                We’ve updated all of the vertices so that the ones near the bottom of the screen have a w of 1 and the
//                ones near the top of the screen have a w of 2; we also updated the line and
//                the mallets to have a fractional w that’s in between. This should have the
//                effect of making the top part of the table appear smaller than the bottom, as
//                if we were looking into the distance. We set all of our z components to zero,
//                since we don’t need to actually have anything in z to get the perspective effect.
//                OpenGL will automatically do the perspective divide for us using the w values
//                that we’ve specified, and our current orthographic projection will just copy
//                these w values over

//                X, Y, R, G, B
                0f, 0f,               1f, 1f, 1f,
                -0.5f, -0.8f,         1.0f, 0.0f, 1.0f,
                0.5f, -0.8f,          0.0f, 1.0f, 0.0f,
                0.5f, 0.8f,           1.0f, 1.0f, 0.0f,
                -0.5f, 0.8f,          0.0f, 0.0f, 1.0f,
                -0.5f, -0.8f,         1.0f, 0.0f, 1.0f,

//                Line 1
                -0.5f, 0f,            1f, 0f, 0f,
                0.5f, 0f,             1f, 0f, 0f,

//                Mallets
                0f, -0.4f,            0f, 0f, 1f,
                0f, 0.4f,             1f, 0f, 0f

        };


        vertexData = ByteBuffer
//        we allocated a block of native memory
//        using ByteBuffer.allocateDirect(); this memory will not be managed by the garbage
//        collector. We need to tell the method how large the block of memory should
//        be in bytes. Since our vertices are stored in an array of floats and there are
//        4 bytes per float, we pass in tableVerticesWithTriangles.length * BYTES_PER_FLOAT.

                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)

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

                .asFloatBuffer();

        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        gl10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

//        Read the shader code

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

//        Link the shaders together

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

//        We call glUseProgram() to tell OpenGL to use the program defined here when
//        drawing anything to the screen.

        GLES20.glUseProgram(program);

//        We call glGetUniformLocation() to get the location of our uniform, and we store
//        that location in uColorLocation. We’ll use that when we want to update the value
//        of this uniform later on.
//        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);

        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);

//        We call glGetAttribLocation() to get the location of our attribute. With this location,
//        we’ll be able to tell OpenGL where to find the data for this attribute.

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);

//        Tell OpenGL where to find data for our attribute a_Position.
//        Before we tell OpenGL to read data from this buffer, we need to make sure
//        that it’ll read our data starting at the beginning and not at the middle or the
//        end. Each buffer has an internal pointer that can be moved by calling
//        position(int), and when OpenGL reads from our buffer, it will start reading at
//        this position. To ensure that it starts reading at the very beginning, we call
//        position(0) to set the position to the beginning of our data.
//        We then call glVertexAttribPointer() to tell OpenGL that it can find the data for
//        a_Position in the buffer vertexData.

        vertexData.position(0);
        GLES20.glVertexAttribPointer(

//                int index: This is the attribute location, and we pass in aPositionLocation to refer to the location that we retrieved earlier
                aPositionLocation,

//                int size: This is the data count per attribute, or how many components are associated with each vertex for this attribute.
                POSITION_COMPONENT_COUNT,

//                int type: This is the type of data.
                GLES20.GL_FLOAT,

//                boolean normalized: This only applies if we use integer data
                false,

//                int stride: applies when we store more than one attribute in a single array.
                STRIDE,

//                Buffer ptr: This tells OpenGL where to read the data. Don’t forget that it will start reading from the buffer’s current position,
//                so if we hadn’t called vertexData.position(0), it would probably
//                try to read past the end of the buffer and crash our application.
                vertexData
        );

//        After calling glVertexAttribPointer(), OpenGL now knows where to read the data
//        for the attribute a_Position.

//        Now that we’ve linked our data to the attribute, we need to enable the attribute
//        with a call to glEnableVertexAttribArray() before we can start drawing.
//        With this final call, OpenGL now knows where to find all the data it needs.
        GLES20.glEnableVertexAttribArray(aPositionLocation);

//        First we set the position of vertexData to POSITION_COMPONENT_COUNT, which is
//        set to 2. Why do we do this? Well, when OpenGL starts reading in the
//        color attributes, we want it to start at the first color attribute, not the first position attribute.
//        We need to skip over the first position ourselves by taking the position
//        component size into account, so we set the position to POSITION_COMPONENT_COUNT
//        so that the buffer’s position is set to the position of the very
//        first color attribute. Had we set the position to 0 instead, OpenGL would
//        be reading in the position as the color.

        vertexData.position(POSITION_COMPONENT_COUNT);

//        We then call glVertexAttribPointer() to associate our color data with a_Color in
//        our shaders. The stride tells OpenGL how many bytes are between each
//        color, so that when it reads in the colors for all of the vertices, it knows
//        how many bytes it needs to skip to read the color for the next vertex. It’s
//        very important that the stride be specified in terms of bytes.
//        Even though a color in OpenGL has four components (red, green, blue, and alpha),
//        we don’t have to specify all of them. Unlike uniforms, OpenGL will replace unspecified
//        components in attributes with defaults: the first
//        three components will be set to 0, and the last component set to 1.

        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);

//        Finally, we enable the vertex attribute for the color attribute, just like we
//        did for the position attribute.

        GLES20.glEnableVertexAttribArray(aColorLocation);


        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
//        Set the OpenGL viewport to fill the entire surface.
        gl10.glViewport(0, 0, width, height);

//        This code will create an orthographic projection matrix that will take the
//        screen’s current orientation into account. It will set up a virtual coordinate space
//        final float aspectRatio = width > height ?
//                (float) width / (float) height :
//                (float) height / (float) width;
//        if (width > height) {
//         Landscape
//            Matrix.orthoM(

//                  float[] m: The destination array — this array’s length should be at least sixteen
//                  elements so it can store the orthographic projection matrix.
//                    projectionMatrix,

//                  int mOffset The offset into m into which the result is written
//                    0,

//                  float left The minimum range of the x-axis
//                    -aspectRatio,

//                  float right The maximum range of the x-axis
//                    aspectRatio,

//                  float bottom The minimum range of the y-axis
//                    -1f,

//                  float top The maximum range of the y-axis
//                    1f,

//                  float near The minimum range of the z-axis
//                    -1f,

//                  float far The maximum range of the z-axis
//                    1f
//            );
//        } else {
//         Portrait or square
//            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
//        }




//        This will create a perspective projection with a field of vision of 45 degrees.
//        The frustum will begin at a z of -1 and will end at a z of -10.

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);

//        This sets the model matrix to the identity matrix and then translates it by -2
//        along the z-axis. When we multiply our air hockey table coordinates with this
//        matrix, they will end up getting moved by 2 units along the negative z-axis.
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);

        Matrix.rotateM(modelMatrix, 0,
                -60f,    // Rotate with this angle
                2f,      // x
                1f,      // y
                2f       // z
        );


//        Whenever we multiply two matrices, we need a temporary area to store the
//        result. If we try to write the result directly, the results are undefined!
        final float[] tmp = new float [16];

//        we call multiplyMM() to multiply the projection matrix and model matrix
//        together into this temporary array.
        Matrix.multiplyMM(tmp, 0, projectionMatrix, 0, modelMatrix, 0);

//        Next we call System.arraycopy() to store the
//        result back into projectionMatrix, which now contains the combined effects of
//        the model matrix and the projection matrix.
        System.arraycopy(tmp, 0, projectionMatrix, 0, tmp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
//        Clear the rendering surface.
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

//        First we update the value of u_Color in our shader code by calling glUniform4f().
//        Unlike attributes, uniforms don’t have default components, so if a uniform
//        is defined as a vec4 in our shader, we need to provide all four components.
//        We want to start out by drawing a white table, so we set red, green, and blue
//        to 1.0f for full brightness. The alpha value doesn’t matter, but we still need
//        to specify it since a color has four components
//        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);


//        Once we’ve specified the color, we then draw our table with a call to glDrawArrays(GLES20.GL_TRIANGLES,0, 6).
//        The first argument tells OpenGL that we want to
//        draw triangles. To draw triangles, we need to pass in at least three vertices
//        per triangle. The second argument tells OpenGL to read in vertices starting
//        at the beginning of our vertex array, and the third argument tells OpenGL to
//        read in six vertices. Since there are three vertices per triangle, this call will
//        end up drawing two triangles.

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);

//        Draw the center dividing line across the middle of the table
//        We set the color to red by passing in 1.0f to the first component (red) and 0.0f to green and blue

//        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);

//        This time we also ask OpenGL to draw lines. We start six
//        vertices after the first vertex and ask OpenGL to draw lines by reading in two
//        vertices. Just like with Java arrays, we’re using zero-based numbering here:
//        0, 1, 2, 3, 4, 5, 6 means that the number 6 corresponds to six vertices after
//        the first vertex, or the seventh vertex. Since there are two vertices per line,
//        we’ll end up drawing one line using these positions:
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);

//        Draw mallets as points
//        Draw the first mallet blue.
//        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);

//        Draw the second mallet red.
//        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);

    }
}
