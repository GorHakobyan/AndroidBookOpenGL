package com.html.gmbrdilos.airhockey.objects;


//    Few requirements for how our object builder should work:
//
//            • The caller can decide how many points the object should have. The more
//              points, the smoother the puck or mallet will look.
//
//            • The object will be contained in one floating-point array. After the object
//              is built, the caller will have one array to bind to OpenGL and one command
//              to draw the object.
//
//            • The object will be centered at the caller’s specified position and will lie
//              flat on the x-z plane. In other words, the top of the object will point straight
//              up.

import com.html.gmbrdilos.airhockey.util.Geometry;

import java.util.ArrayList;
import java.util.List;

public class ObjectBuilder {

//    A constant to represent how many floats we need for a vertex
    private static final int FLOATS_PER_VERTEX = 3;

//    An array to hold these vertices
    private final float[] vertexData;

//    We’ll also need an instance variable to hold the collated draw commands.
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();

//    A variable to keep track of the position in the array for the next vertex
    private int offset = 0;

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

//    We’ll also need to tell OpenGL how to draw the top of the puck. Since a puck
//    is built out of two primitives, a triangle fan for the top and a triangle strip for
//    the side, we need a way to combine these draw commands together so that
//    later on we can just call puck.draw(). One way we can do this is by adding each
//    draw command into a draw list.
    static interface DrawCommand {
        void draw();
    }

//    A method to calculate the size of a cylinder top in vertices:
//    A cylinder top is a circle built out of a triangle fan; it has one vertex in the
//    center, one vertex for each point around the circle, and the first vertex around
//    the circle i0s repeated twice so that we can close the circle off.
    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

//    The following is a method to calculate the size of a cylinder side in vertices:
//    A cylinder side is a rolled-up rectangle built out of a triangle strip, with two
//    vertices for each point around the circle, and with the first two vertices
//    repeated twice so that we can close off the tube.
    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

//    A method to generate Puck
    static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {

//        How many vertices we need to represent the puck.
//        A puck is built out of one cylinder top (equivalent to a circle) and one cylinder side, so
//        the total size in vertices will be equal to:
        int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);

//        Instantiate a new ObjectBuilder with that size
        ObjectBuilder builder = new ObjectBuilder(size);

//        We then calculate where the top of the puck should be:
        Geometry.Circle puckTop = new Geometry.Circle(

//                The puck is vertically centered at center.y, so it’s fine to place the cylinder side
//                there. The cylinder top, however, needs to be placed at the top of the puck.
//                To do that, we move it up by half of the puck’s overall height.
                puck.center.translateY(puck.height / 2f),

                puck.radius);

        builder.appendCircle(puckTop, numPoints);

        builder.appendOpenCylinder(puck, numPoints);

        return builder.build();
    }

    private void appendCircle(Geometry.Circle circle, int numPoints) {

//        Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

//        Fan around center point. <= is used because we want to generate
//        the point at the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++) {

            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);

            vertexData[offset++] = circle.center.x + circle.radius * (float) Math.cos(angleInRadians);

//            Since our circle is going to be lying flat on the x-z plane, the y component of
//            the unit circle maps to our y position.
            vertexData[offset++] = circle.center.y;

            vertexData[offset++] = circle.center.z + circle.radius * (float) Math.sin(angleInRadians);
        }
    }
}
