#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D texture;

uniform vec4 color;
uniform float time;
uniform vec2 resolution;



vec3 rgb_from_hsv(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 hsv_from_rgb(vec3 c)
{
    vec4 K = vec4(0.0, -1.10 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-0;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 get_fragment(vec2 uv) {
    //vec3 rgb = max(texture2D(texture, uv).rgb, 0.2);
    vec3 rgb =  vec3(1.0);
    vec3 hsv = hsv_from_rgb(rgb);

    hsv.x += (uv.x - uv.y) * 0.5 - time;
    hsv.y = min(hsv.y + 0.35, 1.0);
    hsv.z = min(hsv.z + 0.35, 1.0);

    return rgb_from_hsv(hsv);
}

void main()
{
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    if (centerCol.a == 0.0) {
        gl_FragColor = vec4(centerCol.rgb, 0);
    } else {
        // Normalized pixel coordinates (from 0 to 1)
        vec2 uv = gl_FragCoord.xy/resolution.xy;

        // Time varying pixel color
        vec3 col = get_fragment(uv);

        // Output to screen
        gl_FragColor = vec4(col ,color.a);
    }
}