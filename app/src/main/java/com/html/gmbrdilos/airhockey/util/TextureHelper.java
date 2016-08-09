package com.html.gmbrdilos.airhockey.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class TextureHelper {

    private static final String TAG = "TextureHelper";
//    This method will take in an Android context and a resource ID and will return
//    the ID of the loaded OpenGL texture.

    public static int loadTexture(Context context, int resourceId) {

        final int[] textureObjectIds = new int[1];
        GLES20.glGenTextures(
                1, // Number of generated textures
                textureObjectIds, //  OpenGL will store the generated IDs in textureObjectIds.
                0 // Offset
        );


        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }

//        Decompress the image into an Android bitmap

//        We first create a new instance of BitmapFactory.Options called options, and we set
//        inScaled to false. This tells Android that we want the original image data instead
//        of a scaled version of the data.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

//        Read in the resource

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

//        Bind to the texture in OpenGL.

//        Before we can do anything else with our newly generated texture object, we
//        need to tell OpenGL that future texture calls should be applied to this texture
//        object. We do that with a call to glBindTexture():

        GLES20.glBindTexture(
                GLES20.GL_TEXTURE_2D, //  tells OpenGL that this should be treated as a two-dimensional texture
                textureObjectIds[0] // and the second parameter tells OpenGL which texture object ID to bind to
        );

//        Set filtering: a default must be set, or the texture will be black.
//        We set each filter with a call to glTexParameteri()
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, //  GL_TEXTURE_MIN_FILTER refers to minification
                GLES20.GL_LINEAR_MIPMAP_LINEAR // tells OpenGL to use trilinear filtering
        );

        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, // GL_TEXTURE_MAG_FILTER refers to magnification
                GLES20.GL_LINEAR // tells OpenGL to use bilinear filtering
        );

//        Load the bitmap into the bound texture.

//        We can now load the bitmap data into OpenGL with an easy call to GLUtils.texImage2D().
//        This call tells OpenGL to read in the bitmap data defined by bitmap and copy
//        it over into the texture object that is currently bound.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

//        Now that the data’s been loaded into OpenGL, we no longer need to keep the
//        Android bitmap around. Under normal circumstances, it might take a few
//        garbage collection cycles for Dalvik to release this bitmap data, so we should
//        call recycle() on the bitmap object to release the data immediately:
        bitmap.recycle();

//        Generating mipmaps is also a cinch. We can tell OpenGL to generate all of
//        the necessary levels with a quick call to glGenerateMipmap():
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

//        Now that we’ve finished loading the texture, a good practice is to then unbind
//        from the texture so that we don’t accidentally make further changes to this
//        texture with other texture calls.
//        Passing 0 to glBindTexture() unbinds from the current texture.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }
}
