package com.mygdx.game.pokemon.Generator;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.pokemon.Logic.Tile;
import com.mygdx.game.pokemon.Logic.Tile.TILE_TYPE;

public class Level {

	private Tile[][] tiles;
	
	private ArrayList<Room> rooms; 
	
	private TextureRegion texture;
	
	public Level() {
		tiles = new Tile[60][60];
 
		texture = new TextureRegion(new Texture("test.png"));
		
		// Initiates tiles
		int yPos = 0;
		for (int i = 0; i <tiles.length; i++) {
			int xPos = 0;
			for (int j=0; j < tiles[i].length; j++) {
				tiles[i][j]= new Tile(texture); 
				tiles[i][j].setPos(xPos, yPos);
				xPos+=Tile.TILE_WIDTH;
			}
			yPos+=Tile.TILE_WIDTH;
		}
		
		rooms = new ArrayList<Room>();
	}

	public void regenerateTiles() {
		tiles = new Tile[60][60];
		
		
		// Initiates tiles
		int yPos = 0;
		for (int i = 0; i < tiles.length; i++) {
			//System.out.println(i);
			int xPos = 0;
			for (int j=0; j < tiles[i].length; j++) {
				tiles[i][j] = new Tile(texture); 
				tiles[i][j].setPos(xPos, yPos);
				tiles[i][j].setTileType(TILE_TYPE.WALL);
				xPos+=Tile.TILE_WIDTH;
			}
			yPos+=Tile.TILE_WIDTH;
		}
	}
	
	public ArrayList<Room> getRooms() {
		return rooms;
	}

	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}

	public Tile[][] getTiles() {
		return tiles;
	}

}
