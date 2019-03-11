package com.gnarly.game.board.laser;

import org.joml.Vector3f;

public class Cannon {
	
	int x, y;
	Vector3f dir;
	
	public Cannon(int x, int y, Vector3f dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Vector3f getDir() {
		return dir;
	}
}
