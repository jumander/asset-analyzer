#version 150 core

in vec2 texCoord;

out vec4 col;

uniform sampler2D tex;
uniform vec3 color;

void main() {
    
    col = texture(tex, texCoord);

    col = col * vec4(color, 1);// The lazy fox jumped over the brown dog

    
}