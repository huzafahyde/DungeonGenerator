package com.mygdx.game.pokemon.Generator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.pokemon.Logic.Tile;

public class Room {

	private Rectangle rect;

	private int x, y;
	private int width, height;
	
	private Vector2 target; 
	private Vector2 realTarget;
	
	public boolean beginning, first, second, last;
	
	public boolean removed = false;
	 
	public Room(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		target = new Vector2((int)(Math.random() * width), (int)(Math.random() * height));
		realTarget = new Vector2(target.x + x, target.y + y);
		
		// Scales rectangle to grid size
		rect = new Rectangle(x * Tile.TILE_WIDTH, y * Tile.TILE_WIDTH, width * Tile.TILE_WIDTH, height * Tile.TILE_WIDTH);
	}

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Vector2 getTarget() {
		return target;
	}

	public void setTarget(Vector2 target) {
		this.target = target;
	}

	public Vector2 getRealTarget() {
		return realTarget;
	}

	public void setRealTarget(Vector2 realTarget) {
		this.realTarget = realTarget;
	}

}
