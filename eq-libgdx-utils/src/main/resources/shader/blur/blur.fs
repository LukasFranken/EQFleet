#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;
uniform sampler2D u_texture;
uniform vec2 u_direction;   // e.g. (1,0) for horiz, (0,1) for vert
uniform float u_radius;     // how “wide” to blur
const float sigma = 4.0;    // Gaussian sigma

float gaussian(float x) {
    return exp(-0.5 * (x*x)/(sigma*sigma)) / (sigma * sqrt(2.0 * 3.14159));
}

void main() {
    vec4 sum = vec4(0.0);
    float norm = 0.0;
    // sample from -radius to +radius
    for (float i = -u_radius; i <= u_radius; i++) {
        float w = gaussian(i);
        vec2 off = u_direction * i / textureSize(u_texture, 0);
        sum += texture2D(u_texture, v_uv + off) * w;
        norm += w;
    }
    gl_FragColor = sum / norm;
}