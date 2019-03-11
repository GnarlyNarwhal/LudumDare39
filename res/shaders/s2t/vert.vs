#version 330 core

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 iTexCoords;

out vec2 texCoords;

void main() {
	texCoords = iTexCoords;
	gl_Position = projection * view * model * vec4(vertices, 1.0);
}