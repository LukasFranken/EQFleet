#ifdef GL_ES
precision highp float;
#endif

varying vec2 vTexCoord;
uniform sampler2D u_texture;
uniform vec2        dir;        // (1,0)=horiz, (0,1)=vert
uniform float       resolution; // FBO width or height, set per-pass
uniform float       radius;     // blur radius in pixels

void main() {
    vec4 sum = vec4(0.0);
    vec2 tc  = vTexCoord;
    float b = radius / resolution;

    sum += texture2D(u_texture, tc + dir * b * -4.0) * 0.0162162162;
    sum += texture2D(u_texture, tc + dir * b * -3.0) * 0.0540540541;
    sum += texture2D(u_texture, tc + dir * b * -2.0) * 0.1216216216;
    sum += texture2D(u_texture, tc + dir * b * -1.0) * 0.1945945946;
    sum += texture2D(u_texture, tc             ) * 0.2270270270;
    sum += texture2D(u_texture, tc + dir * b *  1.0) * 0.1945945946;
    sum += texture2D(u_texture, tc + dir * b *  2.0) * 0.1216216216;
    sum += texture2D(u_texture, tc + dir * b *  3.0) * 0.0540540541;
    sum += texture2D(u_texture, tc + dir * b *  4.0) * 0.0162162162;

    gl_FragColor = sum;
}
