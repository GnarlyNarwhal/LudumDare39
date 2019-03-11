package com.gnarly.engine.text;

import org.joml.Matrix4f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.model.Vao;
import com.gnarly.engine.shaders.Shader;
import com.gnarly.engine.shaders.Shader2x;
import com.gnarly.engine.texture.Texture;

public class Font {

	private final int
		WIDTH = 5,
		HEIGHT = 8;
	
	private Camera camera;
	private Texture texture;
	private Shader2x shader;
	private Vao vao;
	
	public Font(Camera camera, String fontPath) {
		this.camera = camera;
		texture = new Texture(fontPath);
		shader = Shader.SHADER2X;
		float[] vertices = new float[] {
			0,     0,      0,
			0,     HEIGHT, 0,
			WIDTH, HEIGHT, 0,
			WIDTH, 0,      0
		};
		int[] indices = new int[] {
			0, 1, 3,
			3, 1, 2
		};
		float[] texCoords = new float[] {
			0,       0,
			0,       0.0625f,
			0.0625f, 0.0625f,
			0.0625f, 0
		};
		vao = new Vao(vertices, indices);
		vao.addAttribute(texCoords, 2);
	}
	
	public void drawString(String string, float x, float y, float depth, float height, float r, float g, float b, float a) {
		float scale = height / (float) HEIGHT;
		texture.bind();
		shader.enable();
		shader.setProjection(camera.getProjection());
		shader.setView(camera.getView());
		shader.setColor(r, g, b, a);
		char[] chars = string.toCharArray();
		float lx = x;
		for (char c : chars) {
			if(c == '\n') {
				y += (float) (HEIGHT + 1) * scale;
				lx = x;
			}
			else {
				if(c != ' ') {
					shader.setModel(new Matrix4f().translate(lx, y, depth).scale(scale, scale, 0));
					shader.setChar(c);
					vao.render();
				}
				lx += (float) (WIDTH + 1) * scale;
			}
		}
	}
	
	public float getWidth(float height) {
		return (height / (float) HEIGHT) * (WIDTH + 1);
	}
	
	public float getCharWidth(float height) {
		return (height / (float) HEIGHT) * WIDTH;
	}

	public float getHeight(float height) {
		return (height / (float) HEIGHT) * (HEIGHT + 1);
	}
}
