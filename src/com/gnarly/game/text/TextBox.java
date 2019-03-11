package com.gnarly.game.text;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.rects.ColRect;
import com.gnarly.engine.text.Font;

public class TextBox {

	private static Font font;
	
	private Camera camera;
	private String text;
	
	private ColRect back, body;
	
	private float x, y, depth, width, height, textSize;
	
	public TextBox(Camera camera, String text, float x, float y, float depth, float width, float height, float textSize) {
		this.camera = camera;
		this.text = text;
		this.x = x;
		this.y = y;
		this.depth = depth;
		this.width = width;
		this.height = height;
		this.textSize = textSize;
		back = new ColRect(camera, x, y, depth, width, height, 1, 1, 1, 1);
		body = new ColRect(camera, x + 1, y + 1, depth, width - 2, height - 2, 0, 0, 0, 1);
		if(font == null)
			font = new Font(camera, "res/img/fonts/default.png");
	}
	
	public void render() {
		font.drawString(text, x + 2, y + 2, depth, textSize, 1, 1, 1, 1);
		body.render();
		back.render();
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
