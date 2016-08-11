package com.html.gmbrdilos.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.html.gmbrdilos.airhockey.objects.Mallet;
import com.html.gmbrdilos.airhockey.objects.ObjectBuilder;
import com.html.gmbrdilos.airhockey.objects.Puck;
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

//    We’ll store our view matrix in viewMatrix, and the other two matrices will be
//    used to hold the results of matrix multiplications
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

//        For 2nd texture (BLENDING EXERCISE)
//    private int texture2;

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

//        Each object will be created with 32 points around the circle.
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);

//        For 2nd texture (BLENDING EXERCISE)
//        texture2 = TextureHelper.loadTexture(context, R.drawable.texture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
//        Set the OpenGL viewport to fill the entire surface.
//        gl10.glViewport(0, 0, width, height);
//
////        This code will create an orthographic projection matrix that will take the
////        screen’s current orientation into account. It will set up a virtual coordinate space
////        final float aspectRatio = width > height ?
////                (float) width / (float) height :
////                (float) height / (float) width;
////        if (width > height) {
////         Landscape
////            Matrix.orthoM(
//
////                  float[] m: The destination array — this array’s length should be at least sixteen
////                  elements so it can store the orthographic projection matrix.
////                    projectionMatrix,
//
////                  int mOffset The offset into m into which the result is written
////                    0,
//
////                  float left The minimum range of the x-axis
////                    -aspectRatio,
//
////                  float right The maximum range of the x-axis
////                    aspectRatio,
//
////                  float bottom The minimum range of the y-axis
////                    -1f,
//
////                  float top The maximum range of the y-axis
////                    1f,
//
////                  float near The minimum range of the z-axis
////                    -1f,
//
////                  float far The maximum range of the z-axis
////                    1f
////            );
////        } else {
////         Portrait or square
////            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
////        }
//
//
//
//
////        This will create a perspective projection with a field of vision of 45 degrees.
////        The frustum will begin at a z of -1 and will end at a z of -10.
//
//        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
//
////        This sets the model matrix to the identity matrix and then translates it by -2
////        along the z-axis. When we multiply our air hockey table coordinates with this
////        matrix, they will end up getting moved by 2 units along the negative z-axis.
//        Matrix.setIdentityM(modelMatrix, 0);
//        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
//
//        Matrix.rotateM(modelMatrix, 0,
//                -60f,    // Rotate with this angle
//                1f,      // x
//                0f,      // y
//                0f       // z
//        );
//
//
////        Whenever we multiply two matrices, we need a temporary area to store the
////        result. If we try to write the result directly, the results are undefined!
//        final float[] tmp = new float [16];
//
////        we call multiplyMM() to multiply the projection matrix and model matrix
////        together into this temporary array.
//        Matrix.multiplyMM(tmp, 0, projectionMatrix, 0, modelMatrix, 0);
//
////        Next we call System.arraycopy() to store the
////        result back into projectionMatrix, which now contains the combined effects of
////        the model matrix and the projection matrix.
//        System.arraycopy(tmp, 0, projectionMatrix, 0, tmp.length);

        GLES20.glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);

//        setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY,float centerZ, float upX, float upY, float upZ)
        Matrix.setLookAtM(
//                float[] rm
//                This is the destination array. This array’s length
//                should be at least sixteen elements so that it can
//                store the view matrix.
                viewMatrix,

//                int rmOffset
//                setLookAtM() will begin writing the result at this offset into rm
                0,

//                float eyeX, eyeY, eyeZ
//                This is where the eye will be. Everything in the
//                scene will appear as if we’re viewing it from this
//                point.

//                With an eye of (0, 1.2, 2.2), meaning your eye will be 1.2
//                units above the x-z plane and 2.2 units back. In other words, everything in
//                the scene will appear 1.2 units below you and 2.2 units in front of you.

                0f, 1.2f, 2.2f,

//                float centerX, centerY,centerZ
//                This is where the eye is looking; this position will appear in the center of the scene.

//                 A center of (0, 0, 0) means you’ll be looking down toward the origin in front of you
                0f, 0f, 0f,

//                float upX, upY, upZ
//                If we were talking about your eyes, then this is
//                where your head would be pointing. An upY of 1
//                means your head would be pointing straight up.

//                An up of (0, 1, 0) means that your head will be pointing straight up and the scene won’t be rotated to either side
                0f, 1f, 0f
        );
    }


    @Override
    public void onDrawFrame(GL10 gl10) {
//        Clear the rendering surface.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//        This will cache the results of multiplying the projection and view matrices together into viewProjectionMatrix.
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

////        Draw the table.
////        First we call textureProgram.useProgram() to tell OpenGL to use this program.
//        textureProgram.useProgram();
//
////        Then we pass in the uniforms with a call to textureProgram.setUniforms()
//        textureProgram.setUniforms(projectionMatrix, texture);
//
////        Bind the vertex array data and our shader program with a call to table.bindData().
//        table.bindData(textureProgram);
//
//////        For 2nd texture (BLENDING EXERCISE)
////        GLES20.glUniformMatrix4fv(
////
////               Location
////                textureProgram.getuMatrixLocation(),
////
////               Count
////                1,
////
////               Transpose
////                false,
////
////               Value
////                projectionMatrix,
////
////               Offset
////                0
////        );
////
//////        When we draw using textures
//////        in OpenGL, we don’t pass the texture directly in to the shader. Instead, we
//////        use a texture unit to hold the texture. We do this because a GPU can only
//////        draw so many textures at the same time. It uses these texture units to represent
//////        the active textures currently being drawn.
//////        We can swap textures in and out of texture units if we need to switch textures,
//////        though this may slow down rendering if we do it too often. We can also use
//////        several texture units to draw more than one texture at the same time.
//////        We start out this part by setting the active texture unit to texture unit 0 with a
//////        call to glActiveTexture(), and then we bind our texture to this unit with a call
//////        to glBindTexture(). We then pass in the selected texture unit to u_TextureUnit in the
//////        fragment shader by calling glUniform1i(uTextureUnitLocation, 0).
////
//////        For 2nd texture (BLENDING EXERCISE)
//////       Set the active texture unit to texture unit 1.
////        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
////
//////        For 2nd texture (BLENDING EXERCISE)
//////        Bind the texture to this unit.
////        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2);
////
//////        For 2nd texture (BLENDING EXERCISE)
//////        Tell the texture uniform sampler to use this texture in the shader by
//////        telling it to read from texture unit 1.
////        GLES20.glUniform1i(textureProgram.getuTextureUnitLocation(), 1);
////
//////        For 2nd texture (BLENDING EXERCISE)
////        table.bindData(textureProgram);
//
////        We can then finally draw the table with a call to table.draw()
//        table.draw();
//
////        Draw the mallets.
//        colorProgram.useProgram();
//        colorProgram.setUniforms(projectionMatrix);
//        mallet.bindData(colorProgram);
//        mallet.draw();

        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

//        Draw the mallets.
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

//        2nd Mallet
        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);

//        Note that we don't have to define the object data twice -- we just
//        draw the same mallet again but in a different position and with a
//        different color.

        mallet.draw();

//        Draw the puck.
        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private void positionTableInScene() {
//        The table is defined in terms of X & Y coordinates, so we rotate it
//        90 degrees to lie flat on the XZ plane.
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, -90, 1f, 0f, 0f);

//        The last step is to combine all the matrices together by multiplying viewProjectionMatrix
//        and modelMatrix and storing the result in modelViewProjectionMatrix, which
//        will then get passed into the shader program.
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {

//        The mallets and puck are already defined to lie flat on the x-z plane, so there’s
//        no need for rotation. We translate them based on the parameters passed in
//        so that they’re placed at the proper position above the table.
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, x, y, z);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }
}
