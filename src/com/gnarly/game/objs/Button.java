package com.gnarly.game.objs;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.rects.TexRect;

public class Button {

	public static final int
		UNPRESSED = 0,
		RELEASED  = 1,
		PRESSED   = 2,
		HELD      = 3;
	
	private Window window;
	private Camera camera;
	
	private TexRect[] states;
	
	private float x, y, width, height;
	
	private int state, tex;
	
	public Button(Window window, Camera camera, String tex1, String tex2, String tex3, float x, float y, float depth, float width, float height) {
		this.window = window;
		this.camera = camera;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		states = new TexRect[3];
		states[0] = new TexRect(camera, tex1, x, y, depth, width, height);
		states[1] = new TexRect(camera, tex2, x, y, depth, width, height);
		states[2] = new TexRect(camera, tex3, x, y, depth, width, height);
		tex = 0;
		state = 0;
	}
	
	public void update() {
		if(contains(window.getMouseCoords(camera))) {
			if(window.mousePressed(GLFW_MOUSE_BUTTON_1)) {
				tex = 2;
				if(state <= RELEASED)
					state = PRESSED;
				else
					state = HELD;
			}
			else {
				tex = 1;
				if(state >= PRESSED)
					state = RELEASED;
				else
					state = UNPRESSED;
			}
		}
		else {
			tex = 0;
			state = UNPRESSED;
		}
	}
	
	public void render() {
		states[tex].render();
	}
	
	public boolean contains(Vector3f coords) {
		return coords.x >= x && coords.y >= y && coords.x < x + width && coords.y < y + height;
	}
	
	public int getState() {
		return state;
	}
}
