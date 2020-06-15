#version 150 core

in vec3 pos;
in vec2 tex;

out vec2 texCoord;

void main() {
    texCoord = tex;
    gl_Position = vec4(pos, 1);
}