#ifdef GL_ES
precision highp float;
#endif

varying vec2 vTexCoord;
uniform sampler2D u_texture;

uniform float u_radius;
uniform vec2  u_texelSize;
uniform float u_dropoff;
uniform float u_strength;

void main() {
    vec2 uv = vTexCoord;
    vec4 src = texture2D(u_texture, uv);

    // if already opaque, just output
    if (src.a > 0.999) {
        gl_FragColor = src;
        return;
    }

    // find nearest opaque pixel
    float bestDist = u_radius + 1.0;
    vec3  bestRGB  = vec3(0.0);
    for (float oy = -u_radius; oy <= u_radius; oy += 1.0) {
        for (float ox = -u_radius; ox <= u_radius; ox += 1.0) {
            float d = length(vec2(ox, oy));
            if (d > u_radius) continue;
            vec4 s = texture2D(u_texture, uv + vec2(ox,oy)*u_texelSize);
            if (s.a > 0.999 && d < bestDist) {
                bestDist = d;
                bestRGB  = s.rgb;
            }
        }
    }

    if (bestDist <= u_radius) {
        // normalized distance 0→1
        float nd = bestDist / u_radius;

        // linear alpha
        float a_lin = 1.0 - nd;

        // logarithmic alpha: 1 - log(d+1)/log(r+1)
        float a_log = 1.0 - log(bestDist + 1.0) / log(u_radius + 1.0);

        // mix them by u_dropoff
        float a = mix(a_lin, a_log, clamp(u_dropoff,0.0,1.0));

        gl_FragColor = vec4(bestRGB, a * u_strength);
    } else {
        gl_FragColor = vec4(0.0);
    }
}