#version 150 core

out vec4 col;

uniform vec3 color;

void main() {
    col = vec4(color, 1.0);
}