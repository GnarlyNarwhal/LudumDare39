package com.gnarly.game;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.rects.TexRect;
import com.gnarly.engine.text.Font;
import com.gnarly.game.objs.Button;

public class MenuPanel {

	private Window window;
	private Camera camera;

	private Font font;
	
	private Button play, help, close, next, prev;
	
	private TexRect background;
	private TexRect helpMenu;
	
	private int state = 0, page = 0;
	private boolean showHelp = false;
	private String[] headers, content;
	
	
	public MenuPanel(Window window, Camera camera) {
		this.window = window;
		this.camera = camera;
		font = new Font(camera, "res/img/fonts/default.png");
		background = new TexRect(camera, "res/img/menu/background.png", 0, 0, 0, 640, 360);
		play = new Button(window, camera, "res/img/menu/play/unpressed.png", "res/img/menu/play/hovered.png", "res/img/menu/play/pressed.png", 260, 160, 0, 120, 32);
		help = new Button(window, camera, "res/img/menu/help/unpressed.png", "res/img/menu/help/hovered.png", "res/img/menu/help/pressed.png", 260, 200, 0, 120, 32);
		close = new Button(window, camera, "res/img/menu/close/unpressed.png", "res/img/menu/close/hovered.png", "res/img/menu/close/pressed.png", 384, 110, 0, 16, 16);
		next = new Button(window, camera, "res/img/menu/next/unpressed.png", "res/img/menu/next/hovered.png", "res/img/menu/next/pressed.png", 384, 264, 0, 16, 16);
		prev = new Button(window, camera, "res/img/menu/prev/unpressed.png", "res/img/menu/prev/hovered.png", "res/img/menu/prev/pressed.png", 240, 264, 0, 16, 16);
		helpMenu = new TexRect(camera, "res/img/menu/help/helpMenu.png", 240, 110, 0, 160, 170);
		headers = new String[8];
		headers[0] = "OVERVIEW";
		headers[1] = "GOAL";
		headers[2] = "PROGRAMMING";
		headers[3] = "FIRE";
		headers[4] = "ROTATE";
		headers[5] = "WAIT";
		headers[6] = "THREADS";
		headers[7] = "TIMING";
		content = new String[8];
		content[0] = "SO WHAT THE HECK IS THIS GAME?\n\nWELL BASICALLY ITS A PROGRAMMING\nGAME IN WHICH THE PLAYER CONTROLS\nMIRRORS IN ORDER TO DIRECT A LASER TO\nA TRIGGER.";
		content[1] = "THE GOAL OF THE GAME IS TO BEAT\nALL THE LEVELS. THE EXACT GOAL\nOF EACH LEVEL VARIES BUT THE GOAL\nIS ALWAYS ABOUT TRIGGERING A TRIGGER\nWITH A LASER. WHICH TRIGGER AND AT\nWHAT TIMES ARE LEVEL SPECIFIC.\nTHE BOX AT THE TOP WILL TELL\nYOU THE GOAL FOR THAT LEVEL";
		content[2] = "TO CONTROL THE GAME BOARD YOU USE THE\nPROGRAMMING INTRFACE ON THE LEFT.\n\nTHERE ARE THREE COMMANDS YOU CAN USE.\n\n- FIRE\n- ROTATE\n- WAIT";
		content[3] = "THE FIRE COMMAND DOES EXACTLY WHAT\nIT SAYS. IT FIRES A LASER.\n\nUSAGE:\n\nFIRE X\n\n - X IS THE X COMPONENT OF\n   THE LOCATION OF THE\n   CANNON YOU WANT TO FIRE.";
		content[4] = "THE ROTATE COMMAND ROTATES A MIRROR\n90 DEGREES.\n\nUSAGE:\n\nROTATE X Y\n\n - X IS THE X COMPONENT OF\n   THE LOCATION OF THE\n   THE MIRROR YOU WANT ROTATED\n\n - Y IS THE Y COMPONENT OF\n   THE LOCATION OF THE\n   THE MIRROR YOU WANT ROTATED";
		content[5] = "THE WAIT COMMAND PAUSES THE CURRENT\nTHREAD FOR A CERTAIN NUMBER FO TICKS.\n\nUSAGE:\n\nWAIT X\n\n - X IS THE NUMBER OF TICKS\n   YOU WANT THE THREAD TO PAUSE";
		content[6] = "YOU HAVE FOUR THREADS AT YOUR DISPOAL.\nEACH THREAD CAN ONLY HOUSE A\nCERTAIN NUMBER OF COMMANDS.\nTHE MAIN ADVANTAGE OF MUTIPLE THREADS\nIS IT ALLOWS YOU TO RUN MUTIPLE\nCOMMANDS SIMULTANEOUSLY.";
		content[7] = "THE WHOLE GAME RUNS ON A SYSTEM\nOF TICKS. EACH TICK IS 1/16TH\nOF A SECOND. DURING ONE TICK\nONE COMMAND FROM EACH THREAD\nIS EXECUTED.";
	}
	
	public void update() {
		if(!showHelp) {
			play.update();
			help.update();
			if(play.getState() == Button.RELEASED)
				state = 1;
			else if(help.getState() == Button.RELEASED)
				showHelp = true;
		}
		else {
			close.update();
			next.update();
			prev.update();
			if(close.getState() == Button.RELEASED) {
				page = 0;
				showHelp = false;
			}
			else if(next.getState() == Button.RELEASED && page < headers.length - 1)
				++page;
			else if(prev.getState() == Button.RELEASED && page > 0)
				--page;
		}
	}
	
	public void render() {
		if(!showHelp) {
			play.render();
			help.render();
		}
		else {
			close.render();
			next.render();
			prev.render();
			String pageString = "[" + (page + 1) + "/" + headers.length + "]";
			font.drawString(pageString, (640 - (pageString.length() * 5 + pageString.length() - 1)) / 2, 269, 0, 8, 1, 1, 1, 1);
			font.drawString(headers[page], 243, 113, 0, 16, 1, 1, 1, 1);
			font.drawString(content[page], 243, 136, 0, 5.3333333333f, 1, 1, 1, 1);
			helpMenu.render();
		}
		background.render();
	}
	
	public int getState() {
		int temp = state;
		state = 0;
		return temp;
	}
}
