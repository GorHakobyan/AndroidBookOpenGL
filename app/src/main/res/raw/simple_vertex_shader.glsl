
//     We’ve added a new uniform definition, u_Matrix, and we’ve defined it as a mat4,
//     meaning that this uniform will represent a 4 x 4 matrix.
//     Our matrix to transform our positions

uniform mat4 u_Matrix;

//     The attribute keyword is how we feed these
//     attributes into our shader

attribute vec4 a_Position;
attribute vec4 a_Color;

//     Remember that we said we wanted our colors to vary across the surface of a triangle?
//     Well, this is done by using a special variable type known as a varying.
//     A varying is a special type of variable that blends the values given to it and
//     sends these values to the fragment shader. If a_Color was red at vertex 0 and green at vertex
//     1, then by assigning
//     a_Color to v_Color, we’re telling OpenGL that we want each fragment to receive
//     a blended color. Near vertex 0, the blended color will be mostly red, and as
//     the fragments get closer to vertex 1, the color will start to become green.

varying vec4 v_Color;

void main()
{
    v_Color = a_Color;

//     It also means that our vertex array will no longer be interpreted as normalized device coordinates
//     but will now be interpreted as existing in a virtual coordinate space,
//     as defined by the matrix. The matrix will transform the coordinates from this
//     virtual coordinate space back into normalized device coordinates.

    gl_Position = u_Matrix * a_Position;


//     OpenGL needs us to specify how large each point should appear on the screen
//     By writing to another special output variable, gl_PointSize, we tell OpenGL that
//     the size of the points should be 10. Ten of what, you might ask? Well, when
//     OpenGL breaks the point down into fragments, it will generate fragments in
//     a square that is centered around gl_Position, and the length of each side of this
//     square will be equal to gl_PointSize. The larger gl_PointSize is, the larger the point
//     drawn on the screen.

    gl_PointSize = 10.0;
}