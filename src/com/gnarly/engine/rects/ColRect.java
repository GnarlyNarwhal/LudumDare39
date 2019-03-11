package com.gnarly.engine.rects;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.shaders.Shader;
import com.gnarly.engine.shaders.Shader2c;

public class ColRect extends Rect {
	
	private Shader2c shader;
	
	private float r, g, b, a;
	
	public ColRect(Camera camera, float x, float y, float depth, float width, float height, float r, float g, float b, float a) {
		this.camera = camera;
		if(vao == null)
			initVao();
		position = new Vector3f(x, y, depth);
		rotation = 0;
		sx = width / dims.x;
		sy = height / dims.y;
		flipX = 1;
		flipY = 1;
		shader = Shader.SHADER2C;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void render() {
		shader.enable();
		shader.setProjection(camera.getProjection());
		shader.setView(camera.getView());
		shader.setModel(new Matrix4f().translate(position).translate(dims.mul(0.5f * sx, 0.5f * sy, 1, new Vector3f())).rotateZ(rotation).scale(sx * flipX, sy * flipY, 1).translate(dims.mul(0.5f, new Vector3f()).negate()));
		shader.setColor(r, g, b, a);
		vao.render();
		shader.disable();
	}
}
