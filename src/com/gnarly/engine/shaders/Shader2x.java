package com.gnarly.engine.shaders;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform4f;

public class Shader2x extends Shader {
	
	private int charLoc;
	private int colorLoc;
	
	protected Shader2x() {
		super("res/shaders/s2x/vert.vs", "res/shaders/s2x/frag.fs");
	}

	protected void getUniformLocs() {
		super.getUniformLocs();
		colorLoc = glGetUniformLocation(program, "iColor");
		charLoc = glGetUniformLocation(program, "character");
	}
	
	public void setColor(float r, float g, float b, float a) {
		glUniform4f(colorLoc, r, g, b, a);
	}
	
	public void setChar(int character) {
		int column = character % 16, row = character / 16;
		glUniform2f(charLoc, (float) column / 16f, (float) row / 16f);
	}
}