package com.gnarly.game.text;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import java.util.ArrayList;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.text.Font;
import com.gnarly.game.board.Map;
import com.gnarly.game.commands.Command;

public class EditorManager {
	
	private Window window;
	private Camera camera;
	
	private Font font;
	private StringBuilder input;
	private TextEditor[] boxes;
	
	private int focus;
	
	public EditorManager(Window window, Camera camera) {
		this.window = window;
		this.camera = camera;
		float x = 0, y = 33, width = 180, height = 200;
		font = new Font(camera, "res/img/fonts/default.png");
		input = new StringBuilder();
		focus = 0;
		boxes = new TextEditor[4];
		boxes[0] = new TextEditor(window, camera, font, "THREAD 1", x,                 y,                  0, width / 2f, height / 2f);
		boxes[1] = new TextEditor(window, camera, font, "THREAD 2", x + width / 2 + 1, y,                  0, width / 2f, height / 2f);
		boxes[2] = new TextEditor(window, camera, font, "THREAD 3", x,                 y + height / 2 + 1, 0, width / 2f, height / 2f);
		boxes[3] = new TextEditor(window, camera, font, "THREAD 4", x + width / 2 + 1, y + height / 2 + 1, 0, width / 2f, height / 2f);
	}
	
	public void update() {
		if((window.getKey(GLFW_KEY_LEFT_CONTROL) >= Window.PRESSED || window.getKey(GLFW_KEY_RIGHT_CONTROL) >= Window.PRESSED) && window.getKey(GLFW_KEY_TAB) == Window.PRESSED && focus != -1) {
			++focus;
			if(focus == 4)
				focus = 0;
			for (int i = 0; i < boxes.length; i++) {
				if(i == focus)
					boxes[i].setActive(new Vector3f(0, 0, 0));
				else
					boxes[i].hideCursor();
			}
		}
		if(window.mousePressed(GLFW_MOUSE_BUTTON_1)) {
			boolean contained = false;
			Vector3f mouse = window.getMouseCoords(camera);
			for (int i = 0; i < boxes.length; i++) {
				if(boxes[i].contains(mouse)) {
					boxes[i].setActive(mouse);
					focus = i;
					contained = true;
				}
				else
					boxes[i].hideCursor();
			}
			if(!contained)
				focus = -1; 
		}
		if(focus > -1)
			boxes[focus].update();
	}
	
	public void render() {
		for (int i = 0; i < boxes.length; i++)
			boxes[i].render();
	}
	
	public ArrayList<Command>[] getCommands() {
		ArrayList<Command>[] commands = (ArrayList<Command>[])new ArrayList[4];
		for (int i = 0; i < commands.length; i++)
			commands[i] = boxes[i].getCommands(i + 1);
		return commands;
	}
}
