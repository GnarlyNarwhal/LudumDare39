package com.gnarly.game.board.laser;

import java.util.ArrayList;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.rects.TexRect;
import com.gnarly.engine.texture.Anim;
import com.gnarly.game.Main;
import com.gnarly.game.board.Map;

public class Laser {

	public static final Vector3f
		UP    = new Vector3f(0, -1, 0),
		DOWN  = new Vector3f(0, 1, 0),
		LEFT  = new Vector3f(-1, 0, 0),
		RIGHT = new Vector3f(1, 0, 0);
	
	private final int
		STRAIGHT = 0,
		BEND = 1,
		TRIGGER = 2,
		SOLID = 3;

	private Anim[] fanims;
	private TexRect[] frects;
	private Anim[] banims;
	private TexRect[] brects;
	
	private float startX, startY;
	
	private int fx, fy, ftype, frame;
	private int[] props;
	private Vector3f fdir;
	
	private int bx, by, btype;
	
	private boolean renderFront, renderBack;
	
	private ArrayList<Trigger> triggers;
	private int[][] map;
	
	public Laser(Camera camera, Vector3f dir, float startX, float startY, int x, int y, int[][] map, ArrayList<Trigger> triggers) {
		this.startX = startX;
		this.startY = startY;
		bx = 0;
		by = 0;
		fx = x;
		fy = y;
		fdir = dir;
		this.map = map;
		this.triggers = triggers;
		renderFront = true;
		renderBack = false;
		initAnims(camera);
	}
	
	private void initAnims(Camera camera) {
		banims = new Anim[2];
		banims[0] = new Anim("res/img/map/laser/laserStraightBack.png", 32, 64);
		banims[1] = new Anim("res/img/map/laser/laserBendBack.png", 32, 64);
		brects = new TexRect[2];
		brects[STRAIGHT] = new TexRect(camera, banims[0], 0, 0, 0, 32, 32);
		brects[BEND] = new TexRect(camera, banims[1], 0, 0, 0, 32, 32);
		for (int i = 0; i < banims.length; i++)
			banims[i].pause();
		fanims = new Anim[2];
		fanims[0] = new Anim("res/img/map/laser/laserStraight.png", 32, 64);
		fanims[1] = new Anim("res/img/map/laser/laserBend.png", 32, 64);
		frects = new TexRect[2];
		frects[STRAIGHT] = new TexRect(camera, fanims[0], 0, 0, 0, 32, 32);
		frects[BEND] = new TexRect(camera, fanims[1], 0, 0, 0, 32, 32);
		for (int i = 0; i < fanims.length; i++)
			fanims[i].pause();
	}
	
	public void update() {
		frame = Main.tick % Main.BIG_TICK * (32 / Main.BIG_TICK);
		if(frame == 0) {
			if(bx < 0 || by < 0 || bx >= map.length || by >= map[0].length || getType(bx, by) >= TRIGGER)
				renderBack = false;
			else if(props != null) {
				btype = ftype;
				brects[btype].setPos(startX + bx * 32, startY + by * 32);
				brects[btype].setRotation(props[0]);
				brects[btype].setFlip(1, props[1]);
				renderBack = true;
			}
			if(fx < 0 || fy < 0 || fx >= map.length || fy >= map[0].length || getType(fx, fy) == SOLID)
				renderFront = false;
			else if(getType(fx, fy) == TRIGGER) {
				for (int i = 0; i < triggers.size(); i++) {
					if(triggers.get(i).getX() == fx && triggers.get(i).getY() == fy)
						triggers.get(i).trigger();
				}
				renderFront = false;
			}
			else {
				ftype = getType(fx, fy);
				props = getProps(fx, fy, fdir);
				frects[ftype].setPos(startX + fx * 32, startY + fy * 32);
				frects[ftype].setRotation(props[0]);
				frects[ftype].setFlip(1, props[1]);
				fdir = getDir(fx, fy, fdir);
			}
			bx = fx;
			by = fy;
			fx += fdir.x;
			fy += fdir.y;
		}
	}
	
	private int getType(int x, int y) {
		if(map[x][y] == 0)
			return STRAIGHT;
		else if(map[x][y] >= 1 && map[x][y] <= 2)
			return BEND;
		else if(map[x][y] == 4)
			return TRIGGER;
		else
			return SOLID;
	}
	
	private int[] getProps(int x, int y, Vector3f dir) {
		if((dir.x == 1  && dir.y == 0  && map[x][y] == Map.M45))
			return new int[] {0, 1};
		else if((dir.x == 1  && dir.y == 0  && map[x][y] == Map.M135))
			return new int[] {0, -1};
		else if((dir.x == 1  && dir.y == 0  && map[x][y] == Map.EMPTY))
			return new int[] {90, 1};
		else if((dir.x == -1 && dir.y == 0  && map[x][y] == Map.M45))
			return new int[] {180, 1};
		else if((dir.x == -1 && dir.y == 0  && map[x][y] == Map.M135))
			return new int[] {180, -1};
		else if((dir.x == -1 && dir.y == 0  && map[x][y] == Map.EMPTY))
			return new int[] {90, -1};
		else if((dir.x == 0  && dir.y == 1  && map[x][y] == Map.M45))
			return new int[] {90, -1};
		else if((dir.x == 0  && dir.y == 1  && map[x][y] == Map.M135))
			return new int[] {90, 1};
		else if((dir.x == 0  && dir.y == 1  && map[x][y] == Map.EMPTY))
			return new int[] {0, -1};
		else if((dir.x == 0  && dir.y == -1 && map[x][y] == Map.M45))
			return new int[] {270, -1};
		else if((dir.x == 0  && dir.y == -1 && map[x][y] == Map.M135))
			return new int[] {270, 1};
		else if((dir.x == 0  && dir.y == -1 && map[x][y] == Map.EMPTY))
			return new int[] {0, 1};
		return null;
	}
	
	private Vector3f getDir(int x, int y, Vector3f dir) {
		if((dir.x == 0 && dir.y == 1 && map[x][y] == Map.M45) || (dir.x == 0 && dir.y == -1 && map[x][y] == Map.M135))
			return LEFT;
		else if((dir.x == 0 && dir.y == 1 && map[x][y] == Map.M135) || (dir.x == 0 && dir.y == -1 && map[x][y] == Map.M45))
			return RIGHT;
		else if((dir.x == -1 && dir.y == 0 && map[x][y] == Map.M135) || (dir.x == 1 && dir.y == 0 && map[x][y] == Map.M45))
			return UP;
		else if((dir.x == -1 && dir.y == 0 && map[x][y] == Map.M45) || (dir.x == 1 && dir.y == 0 && map[x][y] == Map.M135))
			return DOWN;
		else if(map[x][y] == Map.EMPTY)
			return dir;
		return null;
	}
	
	public void render() {
		if(renderBack) {
			banims[btype].setFrame(frame);
			brects[btype].render();
		}
		if(renderFront) {
			fanims[ftype].setFrame(frame);
			frects[ftype].render();
		}
	}
	
	public boolean done() {
		return !renderBack && !renderFront;
	}
}
