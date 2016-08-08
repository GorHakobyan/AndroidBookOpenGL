#version 120

uniform mat4 u_Matrix;

attribute vec4 a_Position;

//     a_TextureCoordinates. It’s defined as a vec2 because
//     there are two components: the S coordinate and the T coordinate. We send
//     these coordinates on to the fragment shader as an interpolated varying called
//     v_TextureCoordinates
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;

void main()
{
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position = u_Matrix * a_Position;
}
