#version 150 core

in vec3 pos;

void main() {
    gl_Position = vec4(2*(pos.x-0.5), -2*(pos.y-0.5), pos.z, 1);
}