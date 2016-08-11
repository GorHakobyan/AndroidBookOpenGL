package com.html.gmbrdilos.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.html.gmbrdilos.airhockey.objects.Mallet;
import com.html.gmbrdilos.airhockey.objects.Table;
import com.html.gmbrdilos.airhockey.programs.ColorShaderProgram;
import com.html.gmbrdilos.airhockey.programs.TextureShaderProgram;
import com.html.gmbrdilos.airhockey.util.MatrixHelper;
import com.html.gmbrdilos.airhockey.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;
    private int texture2;

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
//        initialize our new variables.
//        We set the clear color to black, initialize our vertex arrays and shader programs,
//        and load in our texture using the helper function
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        table = new Table();
        mallet = new Mallet();

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
        texture2 = TextureHelper.loadTexture(context, R.drawable.texture);
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
                1f,      // x
                0f,      // y
                0f       // z
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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//        Draw the table.
//        First we call textureProgram.useProgram() to tell OpenGL to use this program.
        textureProgram.useProgram();

//        Then we pass in the uniforms with a call to textureProgram.setUniforms()
        textureProgram.setUniforms(projectionMatrix, texture);

//        Bind the vertex array data and our shader program with a call to table.bindData().
        table.bindData(textureProgram);

        GLES20.glUniformMatrix4fv(

//               Location
                textureProgram.getuMatrixLocation(),

//               Count
                1,

//               Transpose
                false,

//               Value
                projectionMatrix,

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

//        Set the active texture unit to texture unit 1.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);

//        Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2);

//        Tell the texture uniform sampler to use this texture in the shader by
//        telling it to read from texture unit 1.
        GLES20.glUniform1i(textureProgram.getuTextureUnitLocation(), 1);

        table.bindData(textureProgram);
//        We can then finally draw the table with a call to table.draw()
        table.draw();

//        Draw the mallets.
        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();
    }
}
