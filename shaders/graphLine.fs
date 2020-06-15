#version 150 core

out vec4 col;

uniform float x;
uniform float y;
uniform float width;
uniform float height;

uniform float windowWidth;
uniform float windowHeight;

uniform vec3 color;

void main() {

    /*output = vec4(192/255.0, 140/255.0, 94/255.0, 0);*/

    float xPos = gl_FragCoord.x/windowWidth;
    float yPos = 1-gl_FragCoord.y/windowHeight;

    if((xPos > x && xPos < x + width) && (yPos > y && yPos < y + height))
        col = vec4(color, 1);
    else
        col = vec4(0, 0, 0, 0);
}