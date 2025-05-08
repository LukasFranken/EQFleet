#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D u_texture;
uniform float darkshift;
varying vec2 v_texCoord0;

uniform float blurRadius;

void main() {
    vec4 sum = vec4(0.0);
    float totalWeight = 0.0;

    for (int x = -4; x <= 4; x++) {
        for (int y = -4; y <= 4; y++) {
            float weight = 1.0 - abs(float(x) + float(y)) / 8.0;
            vec2 offset = vec2(float(x), float(y)) * blurRadius;
            sum += texture2D(u_texture, v_texCoord0 + offset) * weight;
            totalWeight += weight;
        }
    }

    vec4 sampledColor = sum / totalWeight;
    gl_FragColor = sampledColor * darkshift;
}
