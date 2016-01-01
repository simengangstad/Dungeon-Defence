#ifdef GL_ES
#define LOWP lowp

precision mediump float;

#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform bool u_flash;

void main() {

    vec4 texColor = texture2D(u_texture, v_texCoords);

    if (!u_flash) {

        gl_FragColor = v_color * texColor;
    }
    else {

        gl_FragColor = vec4(1.0, 1.0, 1.0, texColor.a);
    }
}