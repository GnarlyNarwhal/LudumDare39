package com.gnarly.engine.shaders;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.joml.Matrix4f;

public abstract class Shader {

	public static final Shader2a SHADER2A = new Shader2a();
	public static final Shader2c SHADER2C = new Shader2c();
	public static final Shader2t SHADER2T = new Shader2t();
	public static final Shader2x SHADER2X = new Shader2x();
	
	protected int program;
	
	protected int projLoc, viewLoc, modelLoc;
	
	protected Shader(String vertPath, String fragPath) {
		program = glCreateProgram();
		
		int vert = load(vertPath, GL_VERTEX_SHADER);
		int frag = load(fragPath, GL_FRAGMENT_SHADER);
		
		glAttachShader(program, vert);
		glAttachShader(program, frag);
		
		glLinkProgram(program);
		glValidateProgram(program);
		
		glDetachShader(program, vert);
		glDetachShader(program, frag);
		
		glDeleteShader(vert);
		glDeleteShader(frag);
		
		getUniformLocs();
	}
	
	protected Shader(String vertPath, String fragPath, String geomPath) {
		program = glCreateProgram();
		
		int vert = load(vertPath, GL_VERTEX_SHADER);
		int frag = load(fragPath, GL_FRAGMENT_SHADER);
		int geom = load(geomPath, GL_GEOMETRY_SHADER);
		
		glAttachShader(program, vert);
		glAttachShader(program, frag);
		glAttachShader(program, geom);
		
		glLinkProgram(program);
		glValidateProgram(program);
		
		glDetachShader(program, vert);
		glDetachShader(program, frag);
		glDetachShader(program, geom);
		
		glDeleteShader(vert);
		glDeleteShader(frag);
		glDeleteShader(geom);
		
		getUniformLocs();
	}
	
	private int load(String path, int type) {
		StringBuilder file = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line;
			while((line = reader.readLine()) != null)
				file.append(line + '\n');
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String source = file.toString();
		int shader = glCreateShader(type);
		glShaderSource(shader, source);
		glCompileShader(shader);
		if(glGetShaderi(shader, GL_COMPILE_STATUS) != 1)
			throw new RuntimeException("Failed to compile shader: " + path + "! " + glGetShaderInfoLog(shader));
		return shader;
	}
	
	protected void getUniformLocs() {
		projLoc = glGetUniformLocation(program, "projection");
		viewLoc = glGetUniformLocation(program, "view");
		modelLoc = glGetUniformLocation(program, "model");
	}
	
	public void setProjection(Matrix4f projection) { 
		glUniformMatrix4fv(projLoc, false, projection.get(new float[16]));
	}
	
	public void setView(Matrix4f view) { 
		glUniformMatrix4fv(viewLoc, false, view.get(new float[16]));
	}
	
	public void setModel(Matrix4f model) { 
		glUniformMatrix4fv(modelLoc, false, model.get(new float[16]));
	}
	
	public void enable() {
		glUseProgram(program);
	}
	
	public void disable() {
		glUseProgram(0);
	}
}
