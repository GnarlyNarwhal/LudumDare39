package com.gnarly.game;

import java.io.File;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.rects.ColRect;
import com.gnarly.engine.rects.TexRect;
import com.gnarly.engine.text.Font;
import com.gnarly.game.board.Map;
import com.gnarly.game.objs.Button;
import com.gnarly.game.text.Console;
import com.gnarly.game.text.EditorManager;
import com.gnarly.game.text.TextBox;

public class GamePanel {

	private Window window;
	private Camera camera;
	
	private Map map;
	private EditorManager editor;
	private Button run, stop, menu;
	private TexRect header;
	
	private TextBox background;
	private ColRect darkener;
	private Button next, smenu;
	private Font font;
	
	private boolean success;
	private int level = 1;
	private int max;
	
	private int state = 1;
	
	public GamePanel(Window window, Camera camera) {
		max = new File("res/maps").list().length;
		this.window = window;
		this.camera = camera;
		
		map = new Map(camera, "res/maps/level1.llm");
		editor = new EditorManager(window, camera);
		run = new Button(window, camera, "res/img/map/buttons/run/unpressed.png", "res/img/map/buttons/run/hovered.png", "res/img/map/buttons/run/pressed.png", 0, 324, 0, 75, 24);
		stop = new Button(window, camera, "res/img/map/buttons/stop/unpressed.png", "res/img/map/buttons/stop/hovered.png", "res/img/map/buttons/stop/pressed.png", 76, 324, 0, 76, 24);
		menu = new Button(window, camera, "res/img/map/buttons/menu/unpressed.png", "res/img/map/buttons/menu/hovered.png", "res/img/map/buttons/menu/pressed.png", 153, 324, 0, 75, 24);
		header = new TexRect(camera, "res/img/header.png", 0, 0, 0, 228, 32);
		
		background = new TextBox(camera, "", 220, 100, -0.5f, 200, 100, 24);
		darkener = new ColRect(camera, 0, 0, -0.25f, 640, 360, 0, 0, 0, 0.5f);
		next = new Button(window, camera, "res/img/menu/next/unpressed.png", "res/img/menu/next/hovered.png", "res/img/menu/next/pressed.png", 362, 182, -0.75f, 16, 16);
		smenu = new Button(window, camera, "res/img/map/buttons/smenu/unpressed.png", "res/img/map/buttons/smenu/hovered.png", "res/img/map/buttons/smenu/pressed.png", 262, 182, -0.75f, 16, 16);
		font = new Font(camera, "res/img/fonts/default.png");
	}
	
	public void update() {
		if(!success) {
			editor.update();
			map.update();
			run.update();
			stop.update();
			menu.update();
			if(run.getState() == Button.RELEASED) {
				Console.error.clear();
				map.execute(editor.getCommands());
			}
			else if(stop.getState() == Button.RELEASED)
				map.fullStop();
			else if(menu.getState() == Button.RELEASED)
				state = 0;
			success = map.success();
			if(success) {
				++level;
				if(level > max)
					level = 1;
			}
		}
		else {
			next.update();
			smenu.update();
			if(next.getState() == Button.RELEASED) {
				map = new Map(camera, "res/maps/level" + level + ".llm");
				editor = new EditorManager(window, camera);
				success = false;
			}
			else if(smenu.getState() == Button.RELEASED) {
				map = new Map(camera, "res/maps/level" + level + ".llm");
				editor = new EditorManager(window, camera);
				success = false;
				state = 0;
			}
		}
	}
	
	public void render() {
		header.render();
		run.render();
		stop.render();
		menu.render();
		map.render();
		editor.render();
		if(success) {
			font.drawString("SUCCESS!", (640 - font.getWidth(24) * 7 - font.getCharWidth(24)) / 2, 106, -0.75f, 24, 1, 1, 1, 1);
			darkener.render();
			background.render();
			next.render();
			smenu.render();
		}
	}
	
	public int getState() {
		int temp = state;
		state = 1;
		return temp;
	}
}
