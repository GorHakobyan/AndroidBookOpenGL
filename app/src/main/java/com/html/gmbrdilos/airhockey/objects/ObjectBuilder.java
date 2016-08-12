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

import android.opengl.GLES20;

import com.html.gmbrdilos.airhockey.util.Geometry;

import java.util.ArrayList;
import java.util.List;

public class ObjectBuilder
{

//    A constant to represent how many floats we need for a vertex
    private static final int FLOATS_PER_VERTEX = 3;

//    An array to hold these vertices
    private final float[] vertexData;

//    We’ll also need an instance variable to hold the collated draw commands.
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();

//    A variable to keep track of the position in the array for the next vertex
    private int offset = 0;

//    Our constructor initializes the array based on the required size in vertices.
    private ObjectBuilder(int sizeInVertices)
    {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

//    A method to calculate the size of a cylinder top in vertices:
//    A cylinder top is a circle built out of a triangle fan; it has one vertex in the
//    center, one vertex for each point around the circle, and the first vertex around
//    the circle is repeated twice so that we can close the circle off.
    private static int sizeOfCircleInVertices(int numPoints)
    {
        return 1 + (numPoints + 1);
    }

//    The following is a method to calculate the size of a cylinder side in vertices:
//    A cylinder side is a rolled-up rectangle built out of a triangle strip, with two
//    vertices for each point around the circle, and with the first two vertices
//    repeated twice so that we can close off the tube.
    private static int sizeOfOpenCylinderInVertices(int numPoints)
    {
        return (numPoints + 1) * 2;
    }

//    This is just a holder class so that we can return both the vertex data and the
//    draw list in a single object.
    static class GeneratedData
    {

        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList)
        {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    //     We’ll use this to return the generated data inside of a GeneratedData object.
    private GeneratedData build()
    {
        return new GeneratedData(vertexData, drawList);
    }

//    A method to generate Puck:
//    This method creates a new ObjectBuilder with the right array size to hold all of
//    the data for the puck. It also creates a display list so that we can draw
//    the puck later on.
    static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints)
    {

//        How many vertices we need to represent the puck.
//        A puck is built out of one cylinder top (equivalent to a circle) and one cylinder side, so
//        the total size in vertices will be equal to:
        int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);

//        Instantiate a new ObjectBuilder with that size
        ObjectBuilder builder = new ObjectBuilder(size);

//        We then calculate where the top of the puck should be:
//        The puck is vertically centered at center.y, so it’s fine to place the cylinder side
//        there. The cylinder top, however, needs to be placed at the top of the puck.
//        To do that, we move it up by half of the puck’s overall height.
        Geometry.Circle puckTop = new Geometry.Circle(puck.center.translateY(puck.height / 2f), puck.radius);

//        appendCircle() and appendOpenCylinder() to generate
//        the top and sides of the puck. Each method adds its data to vertexData and
//        a draw command to drawList.
        builder.appendCircle(puckTop, numPoints);

        builder.appendOpenCylinder(puck, numPoints);

//        We call build() to return the generated data
        return builder.build();
    }

    static GeneratedData createMallet(Geometry.Point center, float radius, float height, int numPoints)
    {

        int size = sizeOfCircleInVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2;

        ObjectBuilder builder = new ObjectBuilder(size);

//        First, generate the mallet base.
//        Assume 25% base, 75% handle
        float baseHeight = height * 0.25f;

        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;

//        Base
        Geometry.Circle baseCircle = new Geometry.Circle(center.translateY(-baseHeight), radius);

        Geometry.Cylinder baseCylinder = new Geometry.Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight);

        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCylinder, numPoints);

//        Handle
        Geometry.Circle handleCircle = new Geometry.Circle(center.translateY(height * 0.5f), handleRadius);

        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(handleCircle.center.translateY(-handleHeight / 2f), handleRadius, handleHeight);

        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);

        return builder.build();
    }

    private void appendCircle(Geometry.Circle circle, int numPoints)
    {

//        Since we’re only using one array for the object, we need to tell OpenGL the
//        right vertex offsets for each draw command. We calculate the offset and length
//        and store them into startVertex and numVertices.
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

//        Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

//        Fan around center point. <= is used because we want to generate
//        the point at the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++)
        {

            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);

            vertexData[offset++] = circle.center.x + circle.radius * (float) Math.cos(angleInRadians);

//            Since our circle is going to be lying flat on the x-z plane, the y component of
//            the unit circle maps to our y position.
            vertexData[offset++] = circle.center.y;

            vertexData[offset++] = circle.center.z + circle.radius * (float) Math.sin(angleInRadians);
        }

//        With this code, we create a new inner class that calls glDrawArrays() and we add
//        the inner class to our draw list. To draw the puck later, we just have to execute
//        each draw() method in the list.
        drawList.add(new DrawCommand()
        {
            @Override
            public void draw()
            {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints)
    {

        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);

        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);

        for (int i = 0; i <= numPoints; i++)
        {

            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);

            float xPosition = cylinder.center.x + cylinder.radius * (float) Math.cos(angleInRadians);
            float zPosition = cylinder.center.z + cylinder.radius * (float) Math.sin(angleInRadians);

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }

        drawList.add(new DrawCommand()
        {
            @Override
            public void draw()
            {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

//    We’ll also need to tell OpenGL how to draw the top of the puck. Since a puck
//    is built out of two primitives, a triangle fan for the top and a triangle strip for
//    the side, we need a way to combine these draw commands together so that
//    later on we can just call puck.draw(). One way we can do this is by adding each
//    draw command into a draw list.
    interface DrawCommand
    {
        void draw();
    }
}
