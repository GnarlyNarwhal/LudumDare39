package com.gnarly.game.text;

import java.util.ArrayList;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.rects.ColRect;
import com.gnarly.engine.text.Font;
import com.gnarly.game.commands.Command;
import com.gnarly.game.commands.Fire;
import com.gnarly.game.commands.Rotate;
import com.gnarly.game.commands.Wait;

public class TextEditor {

	private static final float
		HEADER_HEIGHT = 10.666666666f,
		TEXT_HEIGHT   =  5.333333333f;
	
	private Window window;
	private Camera camera;
	private Font font;
	private String name;
	
	private ColRect background;
	private ColRect header;
	private ColRect body;
	
	private float x, y, depth, width, height;
	
	private int maxLength;
	private StringBuilder[] contents;
	
	private ColRect cursor;
	private int cx, cy;
	private boolean showing;
	private float lastTime, blinkRate;
	
	public TextEditor(Window window, Camera camera, Font font, String name, float x, float y, float depth, float width, float height) {
		this.window = window;
		this.camera = camera;
		this.font = font;
		this.name = name;
		this.x = x;
		this.y = y;
		this.depth = depth;
		this.width = width;
		this.height = height;
		background = new ColRect(camera, x, y, depth, width, height, 1, 1, 1, 1);
		header = new ColRect(camera, x + 1, y + 1, depth, width - 2, HEADER_HEIGHT, 0, 0, 0, 1);
		body = new ColRect(camera, x + 1, y + HEADER_HEIGHT + 2, depth, width - 2, height - HEADER_HEIGHT - 3, 0, 0, 0, 1);
		maxLength = (int) ((body.getWidth() - 2) / font.getWidth(TEXT_HEIGHT));
		contents = new StringBuilder[(int) ((body.getHeight() - 1) / (TEXT_HEIGHT + 1))];
		for (int i = 0; i < contents.length; i++)
			contents[i] = new StringBuilder();
		cursor = new ColRect(camera, body.getX() + 1, body.getY() + 1, depth, font.getCharWidth(TEXT_HEIGHT), TEXT_HEIGHT, 1, 1, 1, 1); 
		cx = 0;
		cy = 0;
		showing = false;
		lastTime = System.nanoTime() / 1000000f;
		blinkRate = 500;
	}
	
	public void render() {
		if(showing)
			cursor.render();
		font.drawString(name, x + 2, y + 2, depth, HEADER_HEIGHT - 2, 1, 1, 1, 1);
		for (int i = 0; i < contents.length; i++)
			font.drawString(contents[i].toString(), body.getX() + 1, body.getY() + i * (TEXT_HEIGHT + 1) + 1, depth, TEXT_HEIGHT, 1, 1, 1, 1);
		header.render();
		body.render();
		background.render();
	}
	
	public void update() {
		input();
		cursor();
	}
	
	private void input() {
		StringBuilder in = new StringBuilder(window.getInput().toUpperCase());
		if(in.length() > 0) {
			showing = true;
			lastTime = System.nanoTime() / 1000000f;
		}
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			if(c == 10 && cy < contents.length - 1) { // New line
				if(cx == contents[cy].length()) {
					++cy;
					cx = 0;
				}
				else if(contents[contents.length - 1].length() == 0) {
					for (int j = contents.length - 1; j > cy + 1; --j) {
						contents[j].delete(0, contents[j].length());
						contents[j].append(contents[j - 1]);
					}
					contents[cy + 1].delete(0, contents[cy + 1].length());
					contents[cy + 1].append(contents[cy].substring(cx, contents[cy].length()));
					contents[cy].delete(cx, contents[cy].length());
					++cy;
					cx = 0;
				}
			}
			else if(c == 9) {
				--cx;
				for (int j = 0; j < 4 && cx + j <= maxLength; j++)
					contents[cy].insert(++cx, (char) 32);
			}
			else if(c == 8) { // Backspace
				if(cx > 0) { // Mid-line
					contents[cy].deleteCharAt(cx - 1);
					--cx;
				}
				else if(cy > 0) { // Beginning of line
					if(contents[cy - 1].length() + contents[cy].length() <= maxLength) {
						contents[cy - 1].append(contents[cy]);
						for (int j = cy; j < contents.length; j++) {
							contents[j].setLength(0);
							if(j < contents.length - 1)
								contents[j].append(contents[j + 1]);
						}
					}
					--cy;
					cx = contents[cy].length();
				}
			}
			else if(c == 127) { // Delete
				if(cx < contents[cy].length()) // Mid-line
					contents[cy].deleteCharAt(cx);
				else if(cy < contents.length - 1 && contents[cy].length() + contents[cy + 1].length() <= maxLength) { // End of line
					contents[cy].append(contents[cy + 1]);
					for (int j = cy + 1; j < contents.length; j++) {
						contents[j].setLength(0);
						if(j < contents.length - 1)
							contents[j].append(contents[j + 1]);
					}
				}
			}
			else if(c == 1) { // Left arrow
				--cx;
				if(cx == -1) {
					if(cy == 0)
						++cx;
					else {
						--cy;
						cx = contents[cy].length();
					}
				}
			}
			else if(c == 2) { // Right arrow
				++cx;
				if(cx > contents[cy].length()) {
					if(cy == contents.length - 1)
						--cx;
					else {
						++cy;
						cx = 0;
					}
				}
			}
			else if(c == 3 && cy > 0) { // Up arrow
				--cy;
				if(cx > contents[cy].length())
					cx = contents[cy].length();
			}
			else if(c == 4 && cy < contents.length - 1) { // Down arrow
				++cy;
				if(cx > contents[cy].length())
					cx = contents[cy].length();
			}
			else if((range(c, 48, 57) || range(c, 65, 90) || c == 32) && cx < maxLength) {
				contents[cy].insert(cx, c);
				++cx;
			}
		}
		if(contents[cy].length() > maxLength)
			contents[cy].setLength(maxLength);
	}
	
	private void cursor() {
		if(System.nanoTime() / 1000000f - lastTime > blinkRate) {
			showing = !showing;
			lastTime += blinkRate;
		}
		cursor.setPos(body.getX() + cx * font.getWidth(TEXT_HEIGHT) + 1, body.getY() + cy * (TEXT_HEIGHT + 1) + 1);
	}
	
	public boolean contains(Vector3f vector) {
		return vector.x >= this.x && vector.y >= this.y && vector.x < this.x + width && vector.y < this.y + height;
	}
	
	public void setActive(Vector3f coords) {
		int tempY = clampY((int) ((coords.y - body.getY()) / (TEXT_HEIGHT + 1)));
		int tempX = clampX((int) ((coords.x - body.getX()) / font.getWidth(TEXT_HEIGHT)));
		if(tempX > -1 && tempY > -1 && tempX <= maxLength && tempY <= contents.length) {
			cx = tempX;
			cy = tempY + 1;
			while(cy > 0 && contents[--cy].length() == 0);
			if(cx > contents[cy].length())
				cx = contents[cy].length();
		}
		showing = true;
		lastTime = System.nanoTime() / 1000000f;
	}
	
	private int clampX(int x) {
		if(x < 0)
			x = 0;
		else if(x > contents[cy].length())
			x = contents[cy].length();
		return x;
	}
	
	private int clampY(int y) {
		if(y < 0)
			y = 0;
		else if(y > contents.length - 1)
			y = contents.length - 1;
		return y;
	}
	
	public void hideCursor() {
		showing = false;
	}
	
	private boolean range(char c, int start, int end) {
		return c >= start && c <= end;
	}
	
	public ArrayList<Command> getCommands(int thread) {
		ArrayList<Command> commands = new ArrayList<>();
		for (int i = 0; i < contents.length; i++) {
			if(contents[i].length() > 0) {
				if(contents[i].length() >= 6 && contents[i].substring(0, 6).equals("ROTATE")) {
					int x = 0, y = 0;
					String[] split = contents[i].toString().split(" ");
					if(split.length == 3) {
						for (int j = 0; j < split[1].length(); j++) {
							char c = split[1].charAt(j);
							if(c >= 48 && c <= 57) {
								x *= 10;
								x += c - 48;
							}
							else {
								Console.error.add("ROTATE REQUIRES EXACTLY 2 SPACE SEPERATED INTEGERS - THREAD: " + thread + " - LINE " + (i + 1));
								j = contents[i].length();
							}
						}
						for (int j = 0; j < split[2].length(); j++) {
							char c = split[2].charAt(j);
							if(c >= 48 && c <= 57) {
								y *= 10;
								y += c - 48;
							}
							else {
								Console.error.add("ROTATE REQUIRES EXACTLY 2 SPACE SEPERATED INTEGERS - THREAD: " + thread + " - LINE " + (i + 1));
								j = contents[i].length();
							}
						}
					}
					else {
						Console.error.add("ROTATE REQUIRES EXACTLY 2 SPACE SEPERATED INTEGERS - THREAD: " + thread + " - LINE " + (i + 1));
					}
					if(x > 11 || y > 7)
						Console.error.add("CANNOT EXCEED MAP LIMITS OF 12X8 - THREAD: " + thread + " - LINE " + (i + 1));
					else
						commands.add(new Rotate(x, y));
				}
				else if(contents[i].length() >= 4 && contents[i].substring(0, 4).equals("WAIT")) {
					int num = 0;
					for (int j = 5; j < contents[i].length(); ++j) {
						char c = contents[i].charAt(j);
						if(c >= 48 && c <= 57) {
							num *= 10;
							num += c - 48;
						}
						else if(c != 32) {
							Console.error.add("WAIT CANNOT BE FOLLOWED BY NON-NUMERIC CHARACTERS - THREAD: " + thread + " - LINE " + (i + 1));
							j = contents[i].length();
						}
					}
					if(num == 0)
						num = 1;
					commands.add(new Wait(num));
				}
				else if(contents[i].length() >= 6 && contents[i].substring(0, 4).equals("FIRE")) {
					int num = 0;
					for (int j = 5; j < contents[i].length(); ++j) {
						char c = contents[i].charAt(j);
						if(c >= 48 && c <= 57) {
							num *= 10;
							num += c - 48;
						}
						else if(c != 32) {
							Console.error.add("FIRE MUST BE FOLLWED BY NUMBER - THREAD: " + thread + " - LINE: " + (i + 1));
							j = contents[i].length();
						}
					}
					if(num > 99)
						Console.error.add("NUMBER " + num + " IS NOT A VALID NUMBER FOR FIRE - THREAD: " + thread + " - LINE: " + (i + 1));
					else
						commands.add(new Fire(num));
				}
				else
					Console.error.add("UNRECOGNIZED COMMAND: " + (contents[i].toString().length() > 25 ? (contents[i].toString().substring(0, 23) + "...") : contents[i].toString()) + " - THREAD: " + thread + " - LINE: " + (i + 1));
			}
		}
		return commands;
	}
}
