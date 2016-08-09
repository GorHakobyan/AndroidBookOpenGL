package com.html.gmbrdilos.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.html.gmbrdilos.airhockey.data.Mallet;
import com.html.gmbrdilos.airhockey.data.Table;
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
        textureProgram.useProgram();
        textureProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

//        Draw the mallets.
        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();
    }
}
