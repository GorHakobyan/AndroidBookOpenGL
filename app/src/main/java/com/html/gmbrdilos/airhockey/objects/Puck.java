package com.html.gmbrdilos.airhockey.objects;


import com.html.gmbrdilos.airhockey.data.VertexArray;
import com.html.gmbrdilos.airhockey.programs.ColorShaderProgram;
import com.html.gmbrdilos.airhockey.util.Geometry;

import java.util.List;

public class Puck
{

    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius, height;

    private final VertexArray vertexArray;

    private final List<ObjectBuilder.DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck)
    {

//        When a new Puck is created, it will generate the object data, store the vertices
//        in a native buffer with vertexArray, and store the draw list in drawList.
        ObjectBuilder.GeneratedData generatedData =
                ObjectBuilder.createPuck(
                        new Geometry.Cylinder(
                                new Geometry.Point(0f, 0f, 0f),
                                radius,
                                height),
                        numPointsAroundPuck);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

//     Binds the vertex data to the attributes defined by the shader program.
    public void bindData(ColorShaderProgram colorProgram)
    {
        vertexArray.setVertexAttribPointer(
                0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0);
    }

    public void draw()
    {
        for (ObjectBuilder.DrawCommand drawCommand : drawList)
        {
            drawCommand.draw();
        }
    }
}
