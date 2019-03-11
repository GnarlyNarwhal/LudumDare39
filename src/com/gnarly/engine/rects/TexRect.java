package com.gnarly.engine.rects;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.shaders.Shader;
import com.gnarly.engine.shaders.Shader2a;
import com.gnarly.engine.texture.Anim;
import com.gnarly.engine.texture.Texture;

public class TexRect extends Rect {
	
	private Camera camera;
	private Texture texture;
	private Shader shader;
	
	public TexRect(Camera camera, String texPath, float x, float y, float depth, float width, float height) {
		this.camera = camera;
		if(vao == null)
			initVao();
		position = new Vector3f(x, y, depth);
		rotation = 0;
		sx = width / dims.x;
		sy = height / dims.y;
		flipX = 1;
		flipY = 1;
		texture = new Texture(texPath);
		shader = Shader.SHADER2T;
	}
	
	public TexRect(Camera camera, String texPath, int frames, int fps, float x, float y, float depth, float width, float height) {
		this.camera = camera;
		if(vao == null)
			initVao();
		position = new Vector3f(x, y, depth);
		rotation = 0;
		sx = width / dims.x;
		sy = height / dims.y;
		flipX = 1;
		flipY = 1;
		texture = new Anim(texPath, frames, fps);
		shader = Shader.SHADER2A;
	}
	
	public TexRect(Camera camera, Texture texture, float x, float y, float depth, float width, float height) {
		this.camera = camera;
		if(vao == null)
			initVao();
		position = new Vector3f(x, y, depth);
		rotation = 0;
		sx = width / dims.x;
		sy = height / dims.y;
		flipX = 1;
		flipY = 1;
		this.texture = texture;
		shader = Shader.SHADER2T;
	}
	
	public TexRect(Camera camera, Anim anim, float x, float y, float depth, float width, float height) {
		this.camera = camera;
		if(vao == null)
			initVao();
		position = new Vector3f(x, y, depth);
		rotation = 0;
		sx = width / dims.x;
		sy = height / dims.y;
		flipX = 1;
		flipY = 1;
		texture = anim;
		shader = Shader.SHADER2A;
	}
	
	public void render() {
		texture.bind();
		shader.enable();
		shader.setProjection(camera.getProjection());
		shader.setView(camera.getView());
		shader.setModel(new Matrix4f().translate(position).translate(dims.mul(0.5f * sx, 0.5f * sy, 1, new Vector3f())).rotateZ(rotation).scale(sx * flipX, sy * flipY, 1).translate(dims.mul(0.5f, new Vector3f()).negate()));
		if(shader instanceof Shader2a)
			((Shader2a) shader).setAnim((Anim) texture);
		vao.render();
		shader.disable();
		texture.unbind();
	}
	
	public void pause() { 
		((Anim) texture).pause();
	}
	
	public void setFrame(int frame) { 
		((Anim) texture).setFrame(frame);
	}
}
