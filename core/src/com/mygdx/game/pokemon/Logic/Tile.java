package com.mygdx.game.pokemon.Logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {

	public final static int TILE_WIDTH = 24;
	
	private TextureRegion texture;
	
	private int x, y;
	
	private TILE_TYPE tileType;
	private STRUCTURE_TYPE structureType;
	
	private int bitmask = 255; 
	
	public Tile(TextureRegion texture) {
		this.texture = texture; 
		
	}
	
	public enum TILE_TYPE {
		FLOOR, WALL, LIQUID
	}
	
	public enum STRUCTURE_TYPE {
		CORRIDOR, ROOM
	}

	public TextureRegion getTexture() {
		return texture;
	}

	
	public void setTexture(TextureRegion textureRegion) {
		this.texture = textureRegion;
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	public TILE_TYPE getTileType() {
		return tileType;
	}

	public void setTileType(TILE_TYPE tileType) {
		this.tileType = tileType;
	}

	public STRUCTURE_TYPE getStructureType() {
		return structureType;
	}


	public void setStructureType(STRUCTURE_TYPE structureType) {
		this.structureType = structureType;
	}


	public int getBitmask() {
		return bitmask;
	}

	public void setBitmask(int bitmask) {
		this.bitmask = bitmask;
	}

}
