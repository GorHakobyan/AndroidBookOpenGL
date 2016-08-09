
//     To draw the texture on an object, OpenGL will call the fragment shader for
//     each fragment, and each call will receive the texture coordinates in v_TextureCoordinates.
//     The fragment shader will also receive the actual texture data via
//     the uniform u_TextureUnit, which is defined as a sampler2D. This variable type
//     refers to an array of two-dimensional texture data.

precision mediump float;

//     The fragment shader will also receive the actual texture data via
//     the uniform u_TextureUnit, which is defined as a sampler2D. This variable type
//     refers to an array of two-dimensional texture data.
uniform sampler2D u_TextureUnit;

//     OpenGL will call the fragment shader for each fragment, and each
//     call will receive the texture coordinates in v_TextureCoordinates.
varying vec2 v_TextureCoordinates;

void main()
{
//     The interpolated texture coordinates and the texture data are passed in to
//     the shader function texture2D(), which will read in the color value for the texture
//     at that particular coordinate. We then set the fragment to that color by
//     assigning the result to gl_FragColor.
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}
