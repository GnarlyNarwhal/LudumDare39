package com.gnarly.game.commands;

public class Rotate implements Command {

	private int x, y;
	
	public Rotate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String getCommand() {
		return "ROTATE " + x + " " + y;
	}
}
