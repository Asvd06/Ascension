#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

float luma(vec3 color) {
    return dot(color, vec3(0.299, 0.587, 0.114));
}

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float softLuma(vec2 uv) {
    vec2 px = 1.0 / InSize;

    vec2 s = px * 1.15;

    float total = 0.0;

    total += luma(texture(DiffuseSampler, uv).rgb) * 4.0;

    total += luma(texture(DiffuseSampler, uv + vec2( s.x, 0.0)).rgb) * 1.5;
    total += luma(texture(DiffuseSampler, uv + vec2(-s.x, 0.0)).rgb) * 1.5;
    total += luma(texture(DiffuseSampler, uv + vec2(0.0,  s.y)).rgb) * 1.5;
    total += luma(texture(DiffuseSampler, uv + vec2(0.0, -s.y)).rgb) * 1.5;

    total += luma(texture(DiffuseSampler, uv + vec2( s.x,  s.y)).rgb) * 0.75;
    total += luma(texture(DiffuseSampler, uv + vec2(-s.x,  s.y)).rgb) * 0.75;
    total += luma(texture(DiffuseSampler, uv + vec2( s.x, -s.y)).rgb) * 0.75;
    total += luma(texture(DiffuseSampler, uv + vec2(-s.x, -s.y)).rgb) * 0.75;

    return total / 13.0;
}

float edgeAt(vec2 uv, float scale) {
    vec2 px = 1.0 / InSize;
    vec2 stepSize = px * scale;

    float lx0 = softLuma(uv - vec2(stepSize.x, 0.0));
    float lx1 = softLuma(uv + vec2(stepSize.x, 0.0));
    float ly0 = softLuma(uv - vec2(0.0, stepSize.y));
    float ly1 = softLuma(uv + vec2(0.0, stepSize.y));

    return abs(lx1 - lx0) + abs(ly1 - ly0);
}

float vignette(vec2 uv) {
    vec2 c = uv - 0.5;
    float d = dot(c, c);
    return smoothstep(0.68, 0.18, d);
}

void main() {
    vec2 uv = texCoord;

    float coarse = edgeAt(uv, 4.35);
    float medium = edgeAt(uv, 2.15);

    float coarseEdge = smoothstep(0.135, 0.410, coarse);
    float mediumEdge = smoothstep(0.125, 0.360, medium) * 0.28;

    float edge = max(coarseEdge, mediumEdge);
    edge = pow(edge, 1.42);

    float surface = softLuma(uv);

    float faintSurface = smoothstep(0.040, 0.310, surface) * 0.040;

    vec2 starGrid = floor(uv * InSize / 9.0);
    float starNoise = hash(starGrid);
    float starMask = step(0.99885, starNoise);

    float twinkle = 0.50 + 0.50 * sin(Time * 6.0 + starNoise * 31.0);

    vec3 voidBase = vec3(0.004, 0.000, 0.020);
    vec3 dimEdge = vec3(0.105, 0.060, 0.300);
    vec3 brightEdge = vec3(0.470, 0.455, 1.000);
    vec3 surfaceTint = vec3(0.030, 0.020, 0.095);
    vec3 starColor = vec3(0.620, 0.650, 1.000) * starMask * twinkle;

    float vig = vignette(uv);

    vec3 finalColor = voidBase;
    finalColor += surfaceTint * faintSurface;
    finalColor += mix(dimEdge, brightEdge, edge) * edge * 1.04;
    finalColor += starColor * 0.35;

    finalColor *= mix(0.78, 1.08, vig);

    fragColor = vec4(finalColor, 1.0);
}