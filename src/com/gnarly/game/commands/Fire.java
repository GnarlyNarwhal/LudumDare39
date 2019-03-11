package com.gnarly.game.commands;

public class Fire implements Command {

	private int cannon;
	
	public Fire(int cannon) {
		this.cannon = cannon;
	}
	
	public int getCannon() {
		return cannon;
	}
	
	public String getCommand() {
		return "FIRE " + cannon;
	}
}
