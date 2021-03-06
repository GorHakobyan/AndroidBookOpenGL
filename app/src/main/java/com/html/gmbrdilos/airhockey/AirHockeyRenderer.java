package com.html.gmbrdilos.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.html.gmbrdilos.airhockey.objects.Mallet;
import com.html.gmbrdilos.airhockey.objects.Puck;
import com.html.gmbrdilos.airhockey.objects.Table;
import com.html.gmbrdilos.airhockey.programs.ColorShaderProgram;
import com.html.gmbrdilos.airhockey.programs.TextureShaderProgram;
import com.html.gmbrdilos.airhockey.util.Geometry;
import com.html.gmbrdilos.airhockey.util.MatrixHelper;
import com.html.gmbrdilos.airhockey.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyRenderer implements GLSurfaceView.Renderer
{

    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

//    We’ll store our view matrix in viewMatrix, and the other two matrices will be
//    used to hold the results of matrix multiplications
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] invertedViewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

//    Table Bounds
    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;

//    For 2nd texture (BLENDING EXERCISE)
//    private int texture2;

    private boolean malletPressed = false;
    private Geometry.Point blueMalletPosition;
//    We need to keep track of how the mallet is moving over time
    private Geometry.Point previousBlueMalletPosition;

    private Geometry.Point puckPosition;
//    We’ll use the vector to store both the speed and the direction of the puck
    private Geometry.Vector puckVector;

    public AirHockeyRenderer(Context context)
    {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig)
    {
//        initialize our new variables.
//        We set the clear color to black, initialize our vertex arrays and shader programs,
//        and load in our texture using the helper function
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        table = new Table();

//        Each object will be created with 32 points around the circle.
        mallet = new Mallet(0.08f, 0.15f, 32);
        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);
        puck = new Puck(0.06f, 0.02f, 32);

        puckPosition = new Geometry.Point(0f, puck.height / 2f, 0f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);

//        For 2nd texture (BLENDING EXERCISE)
//        texture2 = TextureHelper.loadTexture(context, R.drawable.texture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height)
    {
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
    public void onDrawFrame(GL10 gl10)
    {
//        Clear the rendering surface.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//        Translate the puck by its vector
        puckPosition = puckPosition.translate(puckVector);

//        For Rotation (ROTATION EXCERSISE)
//        rotateM(matrix, offset, speed, xAxis, yAxis, zAxis)
//        Matrix.rotateM(viewMatrix, 0, 0.5f, 0f, 1f, 0f);

//        This will cache the results of multiplying the projection and view matrices together into viewProjectionMatrix.
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

//        This call will create an inverted matrix that we’ll be able to use to convert the two-dimensional touch point into a pair of three-dimensional coordinates.
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);


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
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);

//        Note that we don't have to define the object data twice -- we just
//        draw the same mallet again but in a different position and with a
//        different color.

        mallet.draw();

//        Draw the puck.
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);

//        We first check if the puck has gone either too far to the left or too far to the
//        right. If it has, then we reverse its direction by inverting the x component of
//        the vector.

//        We then check if the puck has gone past the near or far edges of the table.
//        In that case, we reverse its direction by inverting the z component of the
//        vector. Don’t get confused by the z checks—the further away something is,
//        the smaller the z, since negative z points into the distance.

//        Finally, we bring the puck back within the confines of the table by clamping
//        it to the table bounds. If we try things again, our puck should now bounce
//        around inside the table instead of flying off the edge.
        if (puckPosition.x < leftBound + puck.radius || puckPosition.x > rightBound - puck.radius)
        {
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
//            Friction
            puckVector = puckVector.scale(0.9f);
        }

        if (puckPosition.z < farBound + puck.radius || puckPosition.z > nearBound - puck.radius)
        {
            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
//            Friction
            puckVector = puckVector.scale(0.9f);
        }

//        Clamp the puck position.
        puckPosition = new Geometry.Point(
                clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        );

//         Friction
        puckVector = puckVector.scale(0.99f);

        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 0f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private void positionTableInScene()
    {
//        The table is defined in terms of X & Y coordinates, so we rotate it
//        90 degrees to lie flat on the XZ plane.
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, -90, 1f, 0f, 0f);

//        The last step is to combine all the matrices together by multiplying viewProjectionMatrix
//        and modelMatrix and storing the result in modelViewProjectionMatrix, which
//        will then get passed into the shader program.
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z)
    {

//        The mallets and puck are already defined to lie flat on the x-z plane, so there’s
//        no need for rotation. We translate them based on the parameters passed in
//        so that they’re placed at the proper position above the table.
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, x, y, z);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    public void handleTouchPress(float normalizedX, float normalizedY)
    {
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

//        Now test if this ray intersects with the mallet by creating a
//        bounding sphere that wraps the mallet.
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(
                blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z),
                mallet.height / 2f);

//        If the ray intersects (if the user touched a part of the screen that
//        intersects the mallet's bounding sphere), then set malletPressed = true.
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY)
    {
        if (malletPressed)
        {
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

//        Define a plane representing our air hockey table.
            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));

//        Find out where the touched point intersects the plane
//        representing our table. We'll move the mallet along this plane.
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);

            previousBlueMalletPosition = blueMalletPosition;

            blueMalletPosition = new Geometry.Point(

                    clamp(
                            touchedPoint.x,
                            leftBound + mallet.radius,
                            rightBound - mallet.radius),

                    mallet.height / 2f,

                    clamp(
                            touchedPoint.z,
                            0f + mallet.radius,
                            nearBound - mallet.radius)
            );

            float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();

            if (distance < (puck.radius + mallet.radius))
            {
//        The mallet has struck the puck. Now send the puck flying
//        based on the mallet velocity.
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
            }
        }
    }

    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY)
    {
//         We'll convert these normalized device coordinates into world-space
//         coordinates. We'll pick a point on the near and far planes, and draw a
//         line between them. To do this transform, we need to first multiply by
//         the inverse matrix, and then we need to undo the perspective divide.

        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

//        multiply each point with invertedViewProjectionMatrix to get a coordinate in world space
        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

//        There’s an interesting property
//        of the inverted view projection matrix: after we multiply our vertices with the
//        inverted view projection matrix, nearPointWorld and farPointWorld will actually
//        contain an inverted w value. This is because normally the whole point of a
//        projection matrix is to create different w values so that the perspective divide
//        can do its magic; so if we use an inverted projection matrix, we’ll also get an
//        inverted w. All we need to do is divide x, y, and z with these inverted w’s, and
//        we’ll undo the perspective divide.

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);

        Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector)
    {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    private float clamp(float value, float min, float max)
    {
        return Math.min(max, Math.max(value, min));
    }
}
