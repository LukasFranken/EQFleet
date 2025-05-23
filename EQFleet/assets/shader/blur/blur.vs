#ifdef GL_ES
precision mediump float;
#endif

// simple pass-through
attribute vec4 a_position;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
varying vec2 vTexCoord;

void main() {
    vTexCoord = a_texCoord0;
    gl_Position = u_projTrans * a_position;
}
