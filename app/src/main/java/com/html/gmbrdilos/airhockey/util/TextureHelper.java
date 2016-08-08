package com.html.gmbrdilos.airhockey.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

public class TextureHelper {

    private static final String TAG = "TextureHelper";
//    This method will take in an Android context and a resource ID and will return
//    the ID of the loaded OpenGL texture.

    public static int loadTexture(Context context, int resourceId) {

        final int[] textureObjectIds = new int[1];
        GLES20.glGenTextures(
                1,
                textureObjectIds, //  OpenGL will store the generated IDs in textureObjectIds.
                0
        );


        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }


//        We first create a new instance of BitmapFactory.Options called options, and we set
//        inScaled to false. This tells Android that we want the original image data instead
//        of a scaled version of the data.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

//        We then call BitmapFactory.decodeResource() to do the actual decode, passing in
//        the Android context, resource ID, and the decoding options that we’ve just
//        defined. This call will decode the image into bitmap or will return null if it failed.
//        We check against that failure and delete the OpenGL texture object if the
//        bitmap is null. If the decode succeeded, we continue processing the texture

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
            }
            GLES20.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

//        Before we can do anything else with our newly generated texture object, we
//        need to tell OpenGL that future texture calls should be applied to this texture
//        object. We do that with a call to glBindTexture():

        GLES20.glBindTexture(
                GLES20.GL_TEXTURE_2D, //  tells OpenGL that this should be treated as a two-dimensional texture
                textureObjectIds[0] // and the second parameter tells OpenGL which texture object ID to bind to
        );

//        We set each filter with a call to glTexParameteri()
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, //  GL_TEXTURE_MIN_FILTER refers to minification
                GLES20.GL_LINEAR_MIPMAP_LINEAR
        );

        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, // GL_TEXTURE_MAG_FILTER refers to magnification
                GLES20.GL_LINEAR
        );
    }
}
