package com.gnarly.game.board.laser;

import java.util.ArrayList;

import com.gnarly.game.Main;
import com.gnarly.game.text.Console;

public class Trigger {

	private int tick = 0;
	private int frame = 0;
	private int triggered = 0;
	
	private int x, y, met;
	
	private ArrayList<Integer> conditions;
	
	public Trigger(int x, int y) {
		this.x = x;
		this.y = y;
		conditions = new ArrayList<>();
	}
	
	public void update() {
		if(triggered == 2) {
			frame = Main.tick % Main.BIG_TICK * (32 / Main.BIG_TICK);
			if(Main.tick % Main.BIG_TICK == Main.BIG_TICK - 1)
				--triggered;
		}
		else
			frame = 0;
		if(Main.tick % Main.BIG_TICK == 0) {
			boolean check = false;
			for (int i = 0; i < conditions.size(); i++) {
				if(conditions.get(i).intValue() == tick) {
					check = true;
					if(triggered == 0)
						Console.error.add("TRIGGER FAILED TO BE TRIGGERED AT PROPER TIME!");
					else
						++met;				}
			}
			if(!check && triggered == 2) {
				--met;
				Console.error.add("TRIGGER WAS TRIGGERED AT AN UNEXPECTED TIME!");
			}
			++tick;
		}
	}
	
	public void trigger() {
		triggered = 2;
	}
	
	public int getFrame() {
		return frame;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void addCondition(int condition) {
		conditions.add(condition);
	}
	
	public void reset() {
		conditions.clear();
		tick = 0;
		met = 0;
	}
	
	public boolean success() {
		return met == conditions.size();
	}
}
