package com.gnarly.engine.shaders;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;

public class Shader2c extends Shader {
	
	int colorLoc;
	
	protected Shader2c() {
		super("res/shaders/s2c/vert.vs", "res/shaders/s2c/frag.fs");
	}

	protected void getUniformLocs() {
		super.getUniformLocs();
		colorLoc = glGetUniformLocation(program, "iColor");
	}
	
	public void setColor(float r, float g, float b, float a) {
		glUniform4f(colorLoc, r, g, b, a);
	}
}