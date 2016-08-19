package com.html.gmbrdilos.airhockey;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class AirHockeyActivity extends AppCompatActivity
{

    /**
     * Hold a reference to our GLSurfaceView
     */
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    final AirHockeyRenderer airHockeyRenderer = new AirHockeyRenderer(this);

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);

//         Check if the system supports OpenGL ES 2.0.
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
//         Even though the latest emulator supports OpenGL ES 2.0,
//         it has a bug where it doesn't set the reqGlEsVersion so
//         the above check doesn't work. The below will detect if the
//         app is running on an emulator, and assume that it supports
//         OpenGL ES 2.0.
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2)
        {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);

            // Assign our renderer.
            glSurfaceView.setRenderer(airHockeyRenderer); // An Activity is an Android context, so we pass in a reference to this.
            rendererSet = true;
        }

        else
        {
            /*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since
             * we're not doing anything, the app will crash if the device
             * doesn't support OpenGL ES 2.0. If we publish on the market, we
             * should also add the following to AndroidManifest.xml:
             *
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             *
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             */
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }


//        In Android, we can listen in on a view’s touch events by calling setOnTouchListener().
//        When a user touches that view, we’ll receive a call to onTouch().
        glSurfaceView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
//                The first thing we do is check if there’s an event to handle. In Android, the
//                touch events will be in the view’s coordinate space, so the upper left corner
//                of the view will map to (0, 0), and the lower right corner will map to the view’s
//                dimensions. For example, if our view was 480 pixels wide by 800 pixels tall,
//                then the lower right corner would map to (480, 800).
                if (event != null)
                {
//         Convert touch coordinates into normalized device
//         coordinates, keeping in mind that Android's Y
//         coordinates are inverted.
//         Inverting the y-axis and scaling each coordinate into the range [-1, 1].
                    final float normalizedX = (event.getX() / (float) v.getWidth()) * 2 - 1;
                    final float normalizedY = -((event.getY() / (float) v.getHeight()) * 2 - 1);

//                    We check to see if the event is either an initial press or a drag event, because
//                    we’ll need to handle each case differently. An initial press corresponds to
//                    MotionEvent.ACTION_DOWN, and a drag corresponds to MotionEvent.ACTION_MOVE.
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
//                        It’s important to keep in mind that Android’s UI runs in the main thread while
//                        GLSurfaceView runs OpenGL in a separate thread, so we need to communicate
//                        between the two using thread-safe techniques. We use queueEvent() to dispatch
//                        calls to the OpenGL thread, calling airHockeyRenderer.handleTouchPress() for a press
//                        and airHockeyRenderer.handleTouchDrag() for a drag.
                        glSurfaceView.queueEvent(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                airHockeyRenderer.handleTouchPress(normalizedX, normalizedY);
                            }
                        });
                    }

                    else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        glSurfaceView.queueEvent(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                airHockeyRenderer.handleTouchDrag(normalizedX, normalizedY);
                            }
                        });
                    }

                    return true;
                }

//                If the event was null, then we return false.
                else
                {
                    return false;
                }
            }
        });




        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (rendererSet)
        {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (rendererSet)
        {
            glSurfaceView.onResume();
        }
    }
}
