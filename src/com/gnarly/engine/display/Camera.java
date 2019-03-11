package com.gnarly.engine.display;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

	private float width, height;
	private Matrix4f projection;
	private Vector3f position;
	private float rotation;
	
	public Camera(int width, int height) {
		position = new Vector3f();
		rotation = 0;
		projection = new Matrix4f();
		setDims(width, height);
	}
	
	public void setDims(float width, float height) {
		this.width = width;
		this.height = height;
		projection.setOrtho(0, width, height, 0, 1, -1);
	}
	
	public void translate(int x, int y) {
		position.sub(x, y, 0);
	}
	
	public void translate(Vector3f vector) {
		position.sub(vector);
	}
	
	public void setPosition(int x, int y) {
		position.set(x, y, 0);
	}
	
	public void setPosition(Vector3f vector) {
		position.set(vector);
	}
	
	public void rotate(float angle) {
		this.rotation -= angle;
	}
	
	public void setRotation(float angle) {
		this.rotation = angle;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public Matrix4f getProjection() {
		return projection;
	}
	
	public Matrix4f getView() {
		return new Matrix4f().rotateZ(rotation).translate(position);
	}
}
