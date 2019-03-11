package com.gnarly.game.text;

import java.util.ArrayList;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.rects.ColRect;
import com.gnarly.engine.text.Font;

public class Console {

	public static ArrayList<String> error = new ArrayList<>();
	
	private Camera camera;
	
	private ColRect background;
	private ColRect body;
	
	private Font font;
	
	private float x, y, width, height;
	
	ArrayList<String> console;
	
	private int maxLength;
	private int maxHeight;
	private int viewY;
	
	public Console(Camera camera) {
		this.camera = camera;
		x = 0;
		y = 235;
		width = 228;
		height = 88;
		viewY = 0;
		background = new ColRect(camera, x, y, 0, width, height, 1, 1, 1, 1);
		body = new ColRect(camera, x + 1, y + 1, 0, width - 2, height - 2, 0, 0, 0, 1);
		font = new Font(camera, "res/img/fonts/default.png");
		console = new ArrayList<String>();
		maxLength = (int) ((width - 2) / font.getWidth(4));
		maxHeight = (int) ((height - 2) / 5);
	}
	
	public void render() {
		if(error.size() > 0)
			for (int i = 0; i < error.size(); i++) 
				font.drawString(error.get(i), x + 3, y + i * 5 + 3, 0, 4, 1, 0, 0, 1);
		else
			for (int i = viewY; i < console.size() && i < viewY + maxHeight; i++) 
				font.drawString(console.get(i), x + 3, y + (i - viewY) * 5 + 3, 0, 4, 1, 1, 1, 1);
		body.render();
		background.render();
	}
	
	public void printCommands(String[] commands) {
		int perCommand = (int) Math.floor((maxLength - 6) / 4);
		String line = new String();
		for (int i = 0; i < commands.length; i++) {
			String command = commands[i];
			while(command.length() < perCommand)
				command += ' ';
			if(i != 3)
				command += "| ";
			line += command;
		}
		println(line);
	}
	
	public void println(String line) {
		console.add(line);
		if(console.size() > maxHeight && viewY + maxHeight + 1 == console.size())
			++viewY;
	}
	
	public void clear() {
		viewY = 0;
		error.clear();
		console.clear();
	}
}
