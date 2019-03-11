package com.gnarly.game.board;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.rects.TexRect;
import com.gnarly.game.Main;
import com.gnarly.game.board.laser.Cannon;
import com.gnarly.game.board.laser.Laser;
import com.gnarly.game.board.laser.Trigger;
import com.gnarly.game.commands.Command;
import com.gnarly.game.commands.Fire;
import com.gnarly.game.commands.Rotate;
import com.gnarly.game.commands.Wait;
import com.gnarly.game.text.Console;
import com.gnarly.game.text.TextBox;

public class Map {

	public static final int
		EMPTY = 0,
		M45 = 1,
		M135 = 2,
		CANNON = 3,
		TRIGGER = 4;
	
	private static final int
		OFFSET = 14,
		WIDTH = 32,
		HEIGHT = 32,
		COLUMNS = 12,
		ROWS = 10;
	
	private String mapPath;
	
	private Console console;
	
	private Camera camera;
	private TexRect[] states;
	private int map[][];
	
	private TexRect topbar, leftbar;
	private TexRect[] corners, nums;

	private float x, y;

	private TextBox ghead, goals;
	
	private ArrayList<Laser> lasers;
	private ArrayList<Cannon> cannons;
	private ArrayList<Trigger> triggers;
	
	private boolean execute, lastRan;
	private ArrayList<Command>[] commands;
	
	private boolean success = false;
	
	public Map(Camera camera, String mapPath) {
		this.mapPath = mapPath;
		this.camera = camera;
		this.x = camera.getWidth() - getWidth();
		this.y = 0;
		ghead = new TextBox(camera, "GOALS\nTRIG |TICK", 182, 33, 0, 46, 15, 5.3333333f);
		goals = new TextBox(camera, "", 182, 49, 0, 46, 185, 5.3333333f);
		map = new int[COLUMNS][ROWS];
		states = new TexRect[5];
		states[EMPTY]    = new TexRect(camera, "res/img/map/empty.png", 0, 0, 0, 32, 32);
		states[M45]      = new TexRect(camera, "res/img/map/mirror45.png", 0, 0, 0, 32, 32);
		states[M135]     = new TexRect(camera, "res/img/map/mirror135.png", 0, 0, 0, 32, 32);
		states[CANNON]   = new TexRect(camera, "res/img/map/cannon.png", 10, 8, 0, 0, 0, 32, 32);
		states[TRIGGER]  = new TexRect(camera, "res/img/map/trigger.png", 32, 32, 0, 0, 0, 32, 32);
		states[TRIGGER].pause();
		corners = new TexRect[4];
		corners[0] = new TexRect(camera, "res/img/bars/tlcorner.png", x,                      y,                        0, OFFSET, OFFSET);
		corners[1] = new TexRect(camera, "res/img/bars/blcorner.png", x,                      y + getHeight() - OFFSET, 0, OFFSET, OFFSET);
		corners[2] = new TexRect(camera, "res/img/bars/brcorner.png", x + getWidth()- OFFSET, y + getHeight() - OFFSET, 0, OFFSET, OFFSET);
		corners[3] = new TexRect(camera, "res/img/bars/trcorner.png", x + getWidth()- OFFSET, y,                        0, OFFSET, OFFSET);
		topbar  = new TexRect(camera, "res/img/bars/topbar.png",  0, 0, 0, WIDTH,  OFFSET);
		leftbar = new TexRect(camera, "res/img/bars/leftbar.png", 0, 0, 0, OFFSET, HEIGHT);
		lasers = new ArrayList<>();
		cannons = new ArrayList<>();
		triggers = new ArrayList<>();
		nums = new TexRect[12];
		for (int i = 0; i < nums.length; i++)
			nums[i] = new TexRect(camera, "res/img/bars/numbers/" + i + ".png", 0, 0, 0, 8, 8);
		loadMap();
		console = new Console(camera);
	}
	
	public void update() {
		if(Main.tick % Main.BIG_TICK == 0) {
			boolean ran = false;
			if(execute || lasers.size() > 0) {
				ran = true;
				if(commands != null) {
					String[] commandStrings = new String[4];
					for (int i = 0; i < commandStrings.length; i++)
						commandStrings[i] = new String();
					for (int i = 0; i < 4; i++) {
						if(commands[i].size() > 0) {
							commandStrings[i] = commands[i].get(0).getCommand();
							Command command = commands[i].get(0);
							if(command instanceof Wait) {
								Wait wait = (Wait) command;
								wait.decrement();
								if(wait.getTicks() == 0)
									commands[i].remove(0);
							}
							else if(command instanceof Rotate) {
								Rotate rotate = (Rotate) command;
								int x = rotate.getX();
								int y = rotate.getY() + 1;
								if(map[x][y] == M45)
									map[x][y] = M135;
								else if(map[x][y] == M135)
									map[x][y] = M45;
								else {
									Console.error.add("LOCATION " + x + " " + y + " IS NOT A MIRROR");
									execute = false;
								}
								commands[i].remove(0);
							}
							else if(command instanceof Fire) {
								Fire fire = (Fire) command;
								fire(fire.getCannon());
								commands[i].remove(0);
							}
						}
					}
					int num = 0;
					for (int i = 0; i < 4; i++)
						if(commands[i].size() > 0)
							++num;
					if(num == 0) {
						commands = null;
						execute = false;
					}
					console.printCommands(commandStrings);
				}
			}
			if(!ran && lastRan) {
				for (int i = 0; i < triggers.size(); i++) {
					triggers.get(i).update();
					if(!triggers.get(i).success())
						Console.error.add("FAILED TO MEET ALL TRIGGER CONDITIONS!");
				}
				if(Console.error.size() == 0)
					success = true;
				for (int i = 0; i < triggers.size(); i++)
					triggers.get(i).reset();
				fullStop();
			}
			lastRan = ran;
		}
		for (int i = 0; i < lasers.size(); i++)
			lasers.get(i).update();
		for (int i = 0; i < lasers.size(); i++)
			if(lasers.get(i).done())
				lasers.remove(i);
		if(execute || lasers.size() > 0)
			for (int i = 0; i < triggers.size(); i++)
				triggers.get(i).update();
	}
	
	public void render() {
		ghead.render();
		goals.render();
		console.render();
		for (int i = 0; i < lasers.size(); i++)
			lasers.get(i).render();
		for (int i = 0; i < corners.length; i++)
			corners[i].render();
		for (int i = 0; i < map.length; i++) {
			nums[i].setPos(x + i * WIDTH + OFFSET + 12, y + 3);
			nums[i].render();
			topbar.setPos(x + i * WIDTH + OFFSET, y);
			topbar.render();
			nums[i].setPos(x + i * WIDTH + OFFSET + 12, y + getHeight() - OFFSET + 3);
			nums[i].render();
			topbar.setPos(x + i * WIDTH + OFFSET, y + getHeight() - OFFSET);
			topbar.render();
		}
		for (int i = 0; i < map[0].length; i++) {
			if (i > 0 && i < map[0].length - 1) {
				nums[i - 1].setPos(x + 3, y + i * HEIGHT + OFFSET + 12);
				nums[i - 1].render();
				nums[i - 1].setPos(x + getWidth() - OFFSET + 3, y + i * HEIGHT + OFFSET + 12);
				nums[i - 1].render();
			}
			leftbar.setPos(x, y + i * HEIGHT + OFFSET);
			leftbar.render();
			leftbar.setPos(x + getWidth() - OFFSET, y + i * HEIGHT + OFFSET);
			leftbar.render();
		}
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				int state = map[i][j];
				if(state != TRIGGER) {
					states[state].setPos(x + i * WIDTH + OFFSET, y + j * HEIGHT + OFFSET);
					states[state].render();
				}
			}
		}
		for (int i = 0; i < triggers.size(); i++) {
			Trigger trigger = triggers.get(i);
			states[TRIGGER].setFrame(trigger.getFrame());
			states[TRIGGER].setPos(x + trigger.getX() * WIDTH + OFFSET, y + trigger.getY() * HEIGHT + OFFSET);
			states[TRIGGER].render();
		}
	}
	
	public static float getWidth() {
		return WIDTH * COLUMNS + OFFSET * 2;
	}
	
	public static float getHeight() {
		return HEIGHT * ROWS + OFFSET * 2;
	}
	
	private void fire(int cannonNum) {
		Cannon cannon = null;
		for (int i = 0; i < cannons.size() && cannon == null; i++) {
			if(cannons.get(i).getX() == cannonNum)
				cannon = cannons.get(i);
		}
		if(cannon == null)
			Console.error.add("NO CANNON AT POSITION " + cannonNum + "!");
		else {
			int x = cannon.getX();
			int y = cannon.getY();
			Vector3f dir = cannon.getDir();
			x += dir.x;
			y += dir.y;
			lasers.add(new Laser(camera, dir, this.x + OFFSET, this.y + OFFSET, x, y, map, triggers));
		}
	}
	
	private void loadMap() {
		Scanner scanner = null;
		try {
			triggers.clear();
			cannons.clear();
			scanner = new Scanner(new File(mapPath));
			for (int j = 0; j < this.map[0].length; j++) {
				for (int i = 0; i < this.map.length; i++) {
					this.map[i][j] = scanner.nextInt();
					if(this.map[i][j] == CANNON)
						cannons.add(new Cannon(i, j, Laser.UP));
					if(this.map[i][j] == TRIGGER)
						triggers.add(new Trigger(i, j));
				}
			}
			int numGoals = scanner.nextInt();
			String goals = new String();
			for (int i = 0; i < numGoals; i++) {
				String curGoal = new String();
				int trigger = scanner.nextInt();
				int tickNum = scanner.nextInt();
				curGoal += triggers.get(trigger).getX();
				while(curGoal.length() < 4)
					curGoal += ' ';
				curGoal += " |";
				curGoal += tickNum + "\n";
				goals += curGoal;
				triggers.get(trigger).addCondition(tickNum);
			}
			this.goals.setText(goals);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
	}
	
	public void execute(ArrayList<Command>[] commands) {
		if(Console.error.size() == 0) {
			lasers.clear();
			console.clear();
			execute = true;
			this.commands = commands;
		}
	}
	
	public void fullStop() {
		loadMap();
		lasers.clear();
		execute = false;
		commands = null;
	}
	
	public void setMap(String mapPath) {
		this.mapPath = mapPath;
		loadMap();
	}
	
	public int[][] getMap() {
		return map;
	}
	
	public boolean success() {
		return success;
	}
}
