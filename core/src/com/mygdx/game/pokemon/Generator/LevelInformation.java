package com.mygdx.game.pokemon.Generator;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.pokemon.Logic.Tile;

public class LevelInformation {

	private int levelNumber;

	private Texture tileset;

	private ArrayList<TextureRegion> wallTextureRegions;
	private ArrayList<TextureRegion> floorTextureRegions;
	
	private ArrayList<TextureRegion> wallAltTextureRegions1;
	private ArrayList<TextureRegion> wallAltTextureRegions2;
	private ArrayList<TextureRegion> floorAltTextureRegions;

	private int[] skip = { 14, 16, 18, 20, 24, 26, 27, 29, 31, 33, 35, 36, 38, 40, 42, 44, 47, 50, 53, 56, 59, 62, 65,
			68, 71 };
	private int[] inputs = { 56, 248, 224, 62, 255, 227, 14, 143, 131, 40, 136, 160, 34, 0, 10, 130, 32, 8, 170, 128, 2,
			168, 42, 162, 138, 175, 235, 190, 250, 239, 191, 251, 254, 46, 163, 58, 226, 232, 184, 139, 142, 186, 234,
			174, 171, 238, 187};
	private int[] uncommonInputs = {248, 62, 255, 227, 143};
	
	private HashMap<Integer, Integer> commonCodex, uncommonCodex;

	private boolean hasWallAlt;
	private boolean hasGroundAlt;
	private boolean hasLiquidAnim, hasSparkleAnim, hasWallAnim, hasWallSparkleAnim;

	public LevelInformation() {
		levelNumber = 6;
		hasWallAlt =true;
		hasGroundAlt = true;

		wallTextureRegions = new ArrayList<TextureRegion>();
		floorTextureRegions = new ArrayList<TextureRegion>();
		
		wallAltTextureRegions1 = new ArrayList<TextureRegion>();
		wallAltTextureRegions2 = new ArrayList<TextureRegion>();
		floorAltTextureRegions = new ArrayList<TextureRegion>();

		findTileset();

		separateTileset();
	}

	public void disposeTileset() {
		tileset.dispose(); 
	}
	
	private void separateTileset() { 
		separateWalls();
		separateFloors();
		getAltTextures();
	}

	private void separateWalls() {
		// Gets all squares in tileset
		int x = 84;
		int y = 163;
		for (int i = 0; i < 72; i++) {
			TextureRegion textureRegion = new TextureRegion(tileset, x, y, Tile.TILE_WIDTH, Tile.TILE_WIDTH);
			
			wallTextureRegions.add(textureRegion);
			x += Tile.TILE_WIDTH + 1;
			if (x > 84 + 2 * (Tile.TILE_WIDTH + 1)) {
				x = 84;
				y += Tile.TILE_WIDTH + 1;
			}
		}
		
		// Removes hard-coded tiles that are empty
		for (int i = skip.length - 1; i >= 0; i--) {
			wallTextureRegions.remove(skip[i]);
		}
		
		// Creates hard-coded hashmap to find the right tile to place
		commonCodex = new HashMap<Integer, Integer>();
		for (int i = 0; i < inputs.length; i++) {
			commonCodex.put(inputs[i], i);
		}
	}
	
	private void separateFloors() {
		// Gets all squares in tileset
		int x = 309;
		int y = 163;
		for (int i = 0; i < 72; i++) {
			TextureRegion textureRegion = new TextureRegion(tileset, x, y, Tile.TILE_WIDTH, Tile.TILE_WIDTH);
			
			floorTextureRegions.add(textureRegion);
			x += Tile.TILE_WIDTH + 1;
			if (x > 309 + 2 * (Tile.TILE_WIDTH + 1)) {
				x = 309;
				y += Tile.TILE_WIDTH + 1;
			}
		}
		
		// Removes hard-coded tiles that are empty
		for (int i = skip.length - 1; i >= 0; i--) {
			floorTextureRegions.remove(skip[i]);
		}
		
	}

	private void getAltTextures() {
		if (hasWallAlt) {
			int x = 159;
			int y = 163;
			for (int i = 0; i < 8; i++) {
				TextureRegion textureRegion = new TextureRegion(tileset, x, y, Tile.TILE_WIDTH, Tile.TILE_WIDTH);
				
				wallAltTextureRegions1.add(textureRegion);
				x += Tile.TILE_WIDTH + 1;
				if (x > 159 + 2 * (Tile.TILE_WIDTH + 1)) {
					x = 159;
					y += Tile.TILE_WIDTH + 1;
				}
			}
			
			skip = new int[] {0,2,6};
			
			for (int i = skip.length - 1; i >= 0; i--) {
				wallAltTextureRegions1.remove(skip[i]);
			}
			
			x = 234;
			y = 163;
			for (int i = 0; i < 8; i++) {
				TextureRegion textureRegion = new TextureRegion(tileset, x, y, Tile.TILE_WIDTH, Tile.TILE_WIDTH);
				
				wallAltTextureRegions2.add(textureRegion);
				x += Tile.TILE_WIDTH + 1;
				if (x > 234 + 2 * (Tile.TILE_WIDTH + 1)) {
					x = 234;
					y += Tile.TILE_WIDTH + 1;
				}
			}
			
			for (int i = skip.length - 1; i >= 0; i--) {
				wallAltTextureRegions2.remove(skip[i]);
			}
		}
		
		uncommonCodex = new HashMap<Integer, Integer>();
		for (int i = 0; i < uncommonInputs.length; i ++) {
			uncommonCodex.put(uncommonInputs[i], i);
		}
		
		if (hasGroundAlt) {
			floorAltTextureRegions.add(new TextureRegion(tileset, 409, 188, 24, 24));
			floorAltTextureRegions.add(new TextureRegion(tileset, 484, 188, 24, 24));
		}
	}
	
	private void findTileset() {
		tileset = new Texture("Tilesets/" + levelNumber + ".png");
	}

	public int getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}

	public Texture getTileset() {
		return tileset;
	}

	public boolean isHasWallAlt() {
		return hasWallAlt;
	}


	public boolean isHasGroundAlt() {
		return hasGroundAlt;
	}


	public boolean isHasLiquidAnim() {
		return hasLiquidAnim;
	}

	public boolean isHasSparkleAnim() {
		return hasSparkleAnim;
	}

	public boolean isHasWallAnim() {
		return hasWallAnim;
	}

	public boolean isHasWallSparkleAnim() {
		return hasWallSparkleAnim;
	}

	public ArrayList<TextureRegion> getWallTextureRegions() {
		return wallTextureRegions;
	}

	public ArrayList<TextureRegion> getFloorTextureRegions() {
		return floorTextureRegions;
	}

	public ArrayList<TextureRegion> getWallAltTextureRegions1() {
		return wallAltTextureRegions1;
	}

	public ArrayList<TextureRegion> getWallAltTextureRegions2() {
		return wallAltTextureRegions2;
	}

	public ArrayList<TextureRegion> getFloorAltTextureRegions() {
		return floorAltTextureRegions;
	}

	public HashMap<Integer, Integer> getCodex() {
		return commonCodex;
	}

	public HashMap<Integer, Integer> getUncommonCodex() {
		return uncommonCodex;
	}

}
