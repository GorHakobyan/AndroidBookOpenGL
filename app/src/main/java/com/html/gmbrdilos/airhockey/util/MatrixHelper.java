package com.html.gmbrdilos.airhockey.util;

public class MatrixHelper
{

    public static void perspectiveM(
//        Needs to have at least sixteen elements
            float[] m,

            float yFovInDegrees,

//        This should be set to the aspect ratio of the screen, which is equal to width/height.
            float aspect,

//        This should be set to the distance to the near plane and must be
//        positive. For example, if this is set to 1, the near plane will be located
//        at a z of -1.
            float n,

//        This should be set to the distance to the far plane and must be positive and greater than the distance to the near plane.
            float f
    )
    {

        final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180.0);

//        Focal Length  The focal length is calcua
//        lated by 1/tangent of (field of vision/2). The field of vision must be
//        less than 180 degrees.
        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));


//      |  a/aspect      0           0                       0                          |
//      |  0             a           0                       0                          |
//      |  0             0          -((f + n)/(f - n))       -((2 * f * n) / (f - n))   |
//      |  0             0           -1                      0                          |


//        OpenGL stores matrix data in column-major order, which means that we write out data one
//        column at a time rather than one row at a time. The first four values refer to
//        the first column, the second four values to the second column, and so on.

        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;
        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;
        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;
        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;
    }
}
