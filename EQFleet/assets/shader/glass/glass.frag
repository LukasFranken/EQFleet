#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;

uniform vec2 u_quadSize;
uniform vec2 u_panelMin;
uniform vec2 u_panelMax;

uniform vec4 u_fillColor;
uniform vec4 u_edgeColor;
uniform vec4 u_glowColor;

uniform float u_borderSize;
uniform float u_bevelSize;
uniform float u_glowSize;
uniform float u_softness;
uniform float u_reflectionStrength;
uniform float u_glowKAlpha;
uniform float u_glowKRgb;
uniform float u_reflectionY;
uniform float u_reflectionSlope;
uniform float u_reflectionWidth;

uniform int u_mode;

float boxSdf(vec2 p, vec2 halfSize) {
    vec2 d = abs(p) - halfSize;
    return length(max(d, 0.0)) + min(max(d.x, d.y), 0.0);
}

void main() {
    vec2 centerUv = (u_panelMin + u_panelMax) * 0.5;
    vec2 halfSizePx = (u_panelMax - u_panelMin) * 0.5 * u_quadSize;
    vec2 pPx = (v_uv - centerUv) * u_quadSize;

    float sdf = boxSdf(pPx, halfSizePx);
    float insideMask = 1.0 - smoothstep(0.0, u_softness, sdf);

    vec2 panelUvSize = max(u_panelMax - u_panelMin, vec2(0.0001));
    vec2 panelSizePx = panelUvSize * u_quadSize;
    vec2 localUv = clamp((v_uv - u_panelMin) / panelUvSize, 0.0, 1.0);
    vec2 localPx = localUv * panelSizePx;

    float dx = min(localPx.x, panelSizePx.x - localPx.x);
    float dy = min(localPx.y, panelSizePx.y - localPx.y);

    float borderX = 1.0 - smoothstep(u_borderSize - u_softness, u_borderSize + u_softness, dx);
    float borderY = 1.0 - smoothstep(u_borderSize - u_softness, u_borderSize + u_softness, dy);

    float bevelX = 1.0 - smoothstep(u_borderSize, u_borderSize + u_bevelSize, dx);
    float bevelY = 1.0 - smoothstep(u_borderSize, u_borderSize + u_bevelSize, dy);

    float coreX = 1.0 - smoothstep(0.0, u_borderSize * 0.35, dx);
    float coreY = 1.0 - smoothstep(0.0, u_borderSize * 0.35, dy);

    float borderMask = max(borderX, borderY) * insideMask;
    float bevelMask = max(bevelX, bevelY) * insideMask;
    float coreMask = max(coreX, coreY) * insideMask;

    float cornerOverlap = borderX * borderY;
    borderMask *= 1.0 - cornerOverlap * 0.10;
    bevelMask *= 1.0 - cornerOverlap * 0.06;

    vec2 local = (v_uv - u_panelMin) / max(u_panelMax - u_panelMin, vec2(0.0001));

    float band = local.y - u_reflectionY + (local.x - 0.5) * u_reflectionSlope;
	float reflectionMask = exp(-(band * band) / max(u_reflectionWidth * u_reflectionWidth, 0.0001));
	reflectionMask *= insideMask * u_reflectionStrength;
	reflectionMask *= 1.0 - smoothstep(0.38, 0.52, abs(local.x - 0.5));

    float glowT = clamp(sdf / max(u_glowSize, 0.0001), 0.0, 1.0);
    float fadeOutFactorAlpha = 1.0 - (u_glowKAlpha / (glowT + u_glowKAlpha));
    float fadeOutFactorRgb = 1.0 - (u_glowKRgb / (glowT + u_glowKRgb));

    vec3 glowCol = clamp(u_glowColor.rgb - vec3(fadeOutFactorRgb), 0.0, 1.0);
    float glowShape = step(0.0, sdf) * (1.0 - smoothstep(u_glowSize, u_glowSize + u_softness, sdf));
    float glowA = max(u_glowColor.a - fadeOutFactorAlpha, 0.0) * glowShape;

    vec3 fillCol = u_fillColor.rgb;
    vec3 bevelCol = mix(u_edgeColor.rgb, vec3(1.0), 0.20);
    vec3 rimCol = mix(u_edgeColor.rgb, vec3(1.0), 0.55);

    float fillA = u_fillColor.a * insideMask;
    float bevelA = 0.28 * bevelMask * u_edgeColor.a;
    float borderA = 0.55 * borderMask * u_edgeColor.a;
    float coreA = 0.30 * coreMask * u_edgeColor.a;
    float reflA = 0.16 * reflectionMask;

	vec3 panelAccum =
    fillCol * fillA +
    bevelCol * bevelA +
    rimCol * borderA +
    vec3(1.0) * coreA +
    vec3(1.0) * reflA;

	float panelAlpha = clamp(fillA + bevelA + borderA + coreA + reflA, 0.0, 1.0);

	if (u_mode == 0) {
   		vec3 panelRgb = panelAccum / max(panelAlpha, 0.0001);
    	gl_FragColor = vec4(panelRgb, panelAlpha);
	} else {
    	gl_FragColor = vec4(glowCol, glowA);
	}
}