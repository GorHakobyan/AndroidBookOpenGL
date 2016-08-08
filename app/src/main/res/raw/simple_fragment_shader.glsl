
precision mediump float; //Defines the default precision for all floating point data types in the fragment shader

//We can choose between lowp, mediump, and highp, which correspond to low
//precision, medium precision, and high precision. However, highp is only supported
//in the fragment shader on some implementations.
//Why didn’t we have to do this for the vertex shader? The vertex shader can
//also have its default precision changed, but because accuracy is more
//important when it comes to a vertex’s position, the OpenGL designers decided
// to set vertex shaders to the highest setting, highp, by default.

varying vec4 v_Color;

void main()
{
//  blended_value = (vertex_0_value * (100% – distance_ratio)) + (vertex_1_value * distance_ratio)
    gl_FragColor = v_Color;
}
