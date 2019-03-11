package com.gnarly.game.commands;

public class Wait implements Command {
	
	private int ticks;
	
	public Wait(int ticks) {
		this.ticks = ticks;
	}
	
	public int getTicks() {
		return ticks;
	}
	
	public void decrement() {
		--ticks;
	}
	
	public String getCommand() {
		return "WAIT " + ticks;
	}
}
