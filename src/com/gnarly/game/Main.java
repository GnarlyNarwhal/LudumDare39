package com.gnarly.game;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;

public class Main implements Runnable {

	public static final int BIG_TICK = 8;
	
	private final int TICK_RATE = 64;
	private final int FPS = 59;
	
	public static int tick = 0;
	public static int fps = 0;
	
	private int state;
	
	private Window window;
	private Camera camera;
	private MenuPanel menu;
	private GamePanel panel;
	
	public void start() {
		Thread gameLoop = new Thread(this, "OpenGL");
		gameLoop.start();
	}
	
	public void run() {
		long pastTime, curTime, npf = 1000000000 / FPS, pastSec, pastUTime, npt = 1000000000 / TICK_RATE;
		int frames = 0;
		init();
		pastTime = System.nanoTime();
		pastUTime = pastTime;
		pastSec = pastTime;
		while(!window.shouldClose()) {
			curTime = System.nanoTime();
			while(curTime - pastUTime >= npt) {
				update();
				pastUTime += npt;
				++tick;
			}
			curTime = System.nanoTime();
			if(curTime - pastTime > npf) {
				render();
				pastTime += npf;
				++frames;
			}
			if(curTime - pastSec >= 1000000000) {
				fps = frames;
				frames = 0;
				pastSec += 1000000000;
			}
		}
	}
	
	private void init() {
		state = 0;
		window = new Window(false);
		camera = new Camera(640, 360);
		menu = new MenuPanel(window, camera);
		panel = new GamePanel(window, camera);
		window.bind();
	}
	
	private void update() {
		window.update();
		if(state == 0) {
			menu.update();
			state = menu.getState();
		}
		else {
			panel.update();
			state = panel.getState();
		}
	}
	
	private void render() {
		window.clear();
		if(state == 0)
			menu.render();
		else
			panel.render();
		window.bind();
		window.swap();
	}
	
	public static void main(String[] args) {
		new Main().start();
	}
}
