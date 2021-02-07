package com.mygdx.game.pokemon.Generator;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.pokemon.Logic.Tile;
import com.mygdx.game.pokemon.Logic.Tile.STRUCTURE_TYPE;
import com.mygdx.game.pokemon.Logic.Tile.TILE_TYPE;
import com.mygdx.game.pokemon.Math.Edge;
import com.mygdx.game.pokemon.Math.MapRect;

public class LevelGenerator {

	private int tries;
	private int maxRoomWidth, maxRoomHeight;
	private int minRoomWidth, minRoomHeight;

	private float lakeTravelChance = 0.5f;
	private int lakeOrigins = 4;
	
	private int minRoomDistance;

	private Level level;

	private LevelInformation levelInfo;

	private int[] uncommonInputs = { 248, 62, 255, 227, 143 };

	private ArrayList<MapRect> mapRects;
	private ArrayList<Edge> edges;

	public LevelGenerator(Level level) {
		tries = 100;

		minRoomWidth = 7;
		maxRoomWidth = 10;

		minRoomHeight = 5;
		maxRoomHeight = 8;

		minRoomDistance = 2;

		this.level = level;

		levelInfo = new LevelInformation();
	}

	public void generateLevel() {
		level.getRooms().clear();

		// Generates random rooms based on Parameters

		while (level.getRooms().size() < 4) {
			level.regenerateTiles();
			level.getRooms().clear();

			for (int i = 0; i < tries; i++) {
				int width = (int) (Math.random() * (maxRoomWidth - minRoomWidth + 1) + minRoomWidth);
				int height = (int) (Math.random() * (maxRoomHeight - minRoomHeight + 1) + minRoomHeight);

				int floorWidth = level.getTiles()[0].length;
				int floorHeight = level.getTiles().length;

				int x = (int) (Math.random() * floorWidth);
				int y = (int) (Math.random() * floorHeight);

				Room room = new Room(x, y, width, height);
				level.getRooms().add(room);
			}

			checkRectangles();
			if (level.getRooms().size() >= 4) {
				generateCorridors();
				convertTiles();

				countBitmask(TILE_TYPE.WALL);
				countBitmask(TILE_TYPE.FLOOR);
				generateLiquids();
				countBitmask(TILE_TYPE.WALL);
				
				
				generateTextures();
				generateMap();
			}


		}
	}

	private void checkRectangles() {
		checkRectangleBounds();
		checkRectangleDistances();
	}

	private void checkRectangleBounds() {
		// Deletes any rooms that are out of bounds
		for (int i = level.getRooms().size() - 1; i >= 0; i--) {
			Room room = level.getRooms().get(i);
			if (room.getX() + room.getWidth() > level.getTiles()[0].length - 3
					|| room.getY() + room.getHeight() > level.getTiles().length - 3) {
				level.getRooms().remove(i);
			} else if (room.getX() < 3 || room.getY() < 3) {
				level.getRooms().remove(i);
			}
		}
	}

	private void checkRectangleDistances() {
		// Deletes any rectangle that is too close to another
		for (int i = level.getRooms().size() - 1; i >= 0; i--) {

			for (int j = level.getRooms().size() - 1; j >= 0; j--) {

				if (i - j > 0) {
					Room room1 = level.getRooms().get(i);
					Room room2 = level.getRooms().get(j);

					// Takes the second rectangle and expands it to include the minimum distance,
					// ensuring that there are no rectangles too close to each other
					Rectangle editedRect = new Rectangle(room2.getRect().getX() - Tile.TILE_WIDTH * minRoomDistance,
							room2.getRect().getY() - Tile.TILE_WIDTH * minRoomDistance,
							room2.getRect().getWidth() + 2 * Tile.TILE_WIDTH * minRoomDistance,
							room2.getRect().getHeight() + 2 * Tile.TILE_WIDTH * minRoomDistance);

					if (room1.getRect().overlaps(editedRect)) {
						level.getRooms().get(j).removed = true;
					}
				}

			}
		}

		// Deletes marked rectangles
		for (int i = level.getRooms().size() - 1; i >= 0; i--) {
			if (level.getRooms().get(i).removed) {
				level.getRooms().remove(i);
			}
		}
	}

	private void convertTiles() {
		TextureRegion floorTexture = new TextureRegion(new Texture("test2.png"));

		// Any tile within a rectangle will be a floor
		for (int i = 0; i < level.getRooms().size(); i++) {
			for (int row = 0; row < level.getRooms().get(i).getHeight(); row++) {
				for (int col = 0; col < level.getRooms().get(i).getWidth(); col++) {
					int x = level.getRooms().get(i).getY() + row;
					int y = level.getRooms().get(i).getX() + col;

					level.getTiles()[x][y].setTileType(TILE_TYPE.FLOOR);
					level.getTiles()[x][y].setStructureType(STRUCTURE_TYPE.ROOM);

				}
			}
		}
	}

	private void generateCorridors() {

		ArrayList<Room> tempRooms = new ArrayList<Room>(level.getRooms());

		// Picks a random beginning room and removes it from the pool
		int beginning = (int) (Math.random() * (tempRooms.size() - 1));
		Room beginningRoom = tempRooms.get(beginning);
		tempRooms.get(beginning).beginning = true;
		tempRooms.remove(beginning);

		// Always grabs three random remaining rooms and removes them from the pool,
		// designating them as First Stage
		Room[] firstStageRooms = new Room[3];
		for (int i = 0; i < firstStageRooms.length; i++) {
			int firstStage = (int) (Math.random() * (tempRooms.size() - 1));
			Room firstStageRoom = tempRooms.get(firstStage);
			tempRooms.get(firstStage).first = true;
			tempRooms.remove(firstStage);
			firstStageRooms[i] = firstStageRoom;
		}

		// Current Tile the generator is considering
		Vector2 currentTile;

		// List of directions to get to the target
		ArrayList<Vector2> directions;

		// Gets the number of Second Stage rooms for later
		int secondRoomNumber = (tempRooms.size()) / 3;

		for (int i = 0; i < firstStageRooms.length; i++) {
			// For each first stage room, get the set of directions from Beginning Room's
			// target to First Stage Room's target
			directions = new ArrayList<Vector2>(
					getDirections(beginningRoom.getRealTarget(), firstStageRooms[i].getRealTarget()));

			// Sets starting point
			currentTile = new Vector2(beginningRoom.getRealTarget());
			level.getTiles()[(int) currentTile.y][(int) currentTile.x].setTileType(TILE_TYPE.FLOOR);
			level.getTiles()[(int) currentTile.y][(int) currentTile.x].setStructureType(STRUCTURE_TYPE.ROOM);
			;

			// Make corridor between the start and finish using the directions
			makeCorridors(directions, currentTile);

			// Now the Current Tile will be the target of the First Stage Rooms
			currentTile = new Vector2(firstStageRooms[i].getRealTarget());

			// Each First Stage Room connects to the the same amount of Second Stage Rooms
			// This is the floor of the remaining number of rooms divided by three
			Room[] secondStageRooms = new Room[secondRoomNumber];
			for (int j = 0; j < secondRoomNumber; j++) {
				int secondStage = (int) (Math.random() * (tempRooms.size() - 1));
				Room secondStageRoom = tempRooms.get(secondStage);
				secondStageRooms[j] = secondStageRoom;
				tempRooms.get(secondStage).second = true;
				tempRooms.remove(secondStage);

				// Build corridors to Second Stage Rooms
				directions = new ArrayList<Vector2>(getDirections(currentTile, secondStageRooms[j].getRealTarget()));
				makeCorridors(directions, currentTile);

			}
		}

		// Connect the final one or two rooms if they exist to the Second Stage Rooms
		for (int i = 0; i < tempRooms.size(); i++) {
			currentTile = new Vector2(tempRooms.get(i).getRealTarget());
			directions = new ArrayList<Vector2>(getDirections(currentTile, firstStageRooms[i].getRealTarget()));
			makeCorridors(directions, currentTile);
		}
	}

	private void makeCorridors(ArrayList<Vector2> directions, Vector2 currentTile) {
		// Iterates through number of vector directions
		for (int j = 0; j < directions.size(); j++) {

			// If the x direction is not already "empty"
			if (directions.get(j).x != 0) {

				// If going right
				if (directions.get(j).x > 0) {

					// Move the Current Tile right until direction is over
					for (int k = 0; k < directions.get(j).x; k++) {
						currentTile.x += 1;
						level.getTiles()[(int) currentTile.y][(int) currentTile.x].setTileType(TILE_TYPE.FLOOR);
						level.getTiles()[(int) currentTile.y][(int) currentTile.x]
								.setStructureType(STRUCTURE_TYPE.CORRIDOR);
					}

					// If going left
				} else if (directions.get(j).x < 0) {

					// Move the Current Tile left until direction is over
					for (int k = 0; k > directions.get(j).x; k--) {
						currentTile.x -= 1;
						level.getTiles()[(int) currentTile.y][(int) currentTile.x].setTileType(TILE_TYPE.FLOOR);
						level.getTiles()[(int) currentTile.y][(int) currentTile.x]
								.setStructureType(STRUCTURE_TYPE.CORRIDOR);
					}

				}

				// If the y direction is not already "empty"
			} else if (directions.get(j).y != 0) {

				// If going up
				if (directions.get(j).y > 0) {

					// Move the current tile up until direction is over
					for (int k = 0; k < directions.get(j).y; k++) {
						currentTile.y += 1;
						level.getTiles()[(int) currentTile.y][(int) currentTile.x].setTileType(TILE_TYPE.FLOOR);
						level.getTiles()[(int) currentTile.y][(int) currentTile.x]
								.setStructureType(STRUCTURE_TYPE.CORRIDOR);

					}
					// If going down
				} else if (directions.get(j).y < 0) {

					// Move the current tile down until direction is over
					for (int k = 0; k > directions.get(j).y; k--) {
						currentTile.y -= 1;
						level.getTiles()[(int) currentTile.y][(int) currentTile.x].setTileType(TILE_TYPE.FLOOR);
						level.getTiles()[(int) currentTile.y][(int) currentTile.x]
								.setStructureType(STRUCTURE_TYPE.CORRIDOR);

					}
				}
			}
		}
	}

	private ArrayList<Vector2> getDirections(Vector2 start, Vector2 end) {

		boolean left = false;
		boolean right = false;
		boolean up = false;
		boolean down = false;

		// Find out where the end destination is relative to the start
		if (end.x > start.x) {
			right = true;
		} else if (end.x < start.x) {
			left = true;
		}
		if (end.y > start.y) {
			up = true;
		} else if (end.y < start.y) {
			down = true;
		}

		ArrayList<Vector2> directions = new ArrayList<Vector2>();

		Vector2 distance = getDistance(start, end);

		// Repeat until both are zero
		while (distance.x != 0 || distance.y != 0) {
			if (Math.random() > 0.5d) {
				// Vertical
				if (distance.y != 0) {
					int steps = 0;
					// Generate random direction vertically and subtract it from the total distance
					if (up) {
						steps = (int) (Math.random() * (distance.y - 1) + 1);
					} else if (down) {
						steps = (int) (Math.random() * (distance.y + 1) - 1);
					}
					Vector2 direction = new Vector2(0, steps);
					distance.y -= steps;
					directions.add(direction);
				}

			} else {
				// Horizontal
				if (distance.x != 0) {
					int steps = 0;
					// Generate random direction horizontally and subtract it from the total
					// distance
					if (right) {
						steps = (int) (Math.random() * (distance.x - 1) + 1);
					} else if (left) {
						steps = (int) (Math.random() * (distance.x + 1) - 1);
					}
					Vector2 direction = new Vector2(steps, 0);
					distance.x -= steps;
					directions.add(direction);
				}
			}
		}

		return directions;
	}

	private Vector2 getDistance(Vector2 start, Vector2 end) {
		return new Vector2(end.x - start.x, end.y - start.y);
	}

	private void generateTextures() {
		// Set the right texture based on bitmasking and hashmap
		for (int i = 0; i < level.getTiles().length; i++) {
			for (int j = 0; j < level.getTiles()[i].length; j++) {
				if (level.getTiles()[i][j].getTileType() == TILE_TYPE.WALL) {
					if (levelInfo.isHasWallAlt()) {
						double random = Math.random();
						boolean flag = false;
						for (int k = 0; k < uncommonInputs.length; k++) {
							if (level.getTiles()[i][j].getBitmask() == uncommonInputs[k]) {
								flag = true;
								break;
							}
						}

						if (flag) {
							if (random < 0.7f) {
								level.getTiles()[i][j].setTexture(levelInfo.getWallTextureRegions()
										.get(levelInfo.getCodex().get(level.getTiles()[i][j].getBitmask())));

							} else if (random < 0.9f) {
								level.getTiles()[i][j].setTexture(levelInfo.getWallAltTextureRegions1()
										.get(levelInfo.getUncommonCodex().get(level.getTiles()[i][j].getBitmask())));

							} else {
								level.getTiles()[i][j].setTexture(levelInfo.getWallAltTextureRegions2()
										.get(levelInfo.getUncommonCodex().get(level.getTiles()[i][j].getBitmask())));
							}

						} else {
							level.getTiles()[i][j].setTexture(levelInfo.getWallTextureRegions()
									.get(levelInfo.getCodex().get(level.getTiles()[i][j].getBitmask())));
						}

					} else {
						level.getTiles()[i][j].setTexture(levelInfo.getWallTextureRegions()
								.get(levelInfo.getCodex().get(level.getTiles()[i][j].getBitmask())));
					}
				}
			}
		}

		for (int i = 0; i < level.getTiles().length; i++) {
			for (int j = 0; j < level.getTiles()[i].length; j++) {
				if (level.getTiles()[i][j].getTileType() == TILE_TYPE.FLOOR) {
					if (levelInfo.isHasGroundAlt() && level.getTiles()[i][j].getBitmask() == 255) {
						double random = Math.random();
						if (random < 0.8f) {
							level.getTiles()[i][j].setTexture(levelInfo.getFloorTextureRegions()
									.get(levelInfo.getCodex().get(level.getTiles()[i][j].getBitmask())));

						} else if (random < 0.9f) {
							level.getTiles()[i][j].setTexture(levelInfo.getFloorAltTextureRegions().get(0));

						} else {
							level.getTiles()[i][j].setTexture(levelInfo.getFloorAltTextureRegions().get(1));
						}

					} else {

						level.getTiles()[i][j].setTexture(levelInfo.getFloorTextureRegions()
								.get(levelInfo.getCodex().get(level.getTiles()[i][j].getBitmask())));
					}
				}
			}
		}
	}

	private void countBitmask(TILE_TYPE detection) {
		for (int i = 0; i < level.getTiles().length; i++) {
			for (int j = 0; j < level.getTiles()[i].length; j++) {

				// For each tile, see whether the tile above, right, below and left are the same
				// tile or out of bounds. If so set the corresponding boolean to be true and set
				// bool to be 1
				int bitmask = 0;

				boolean up = false;
				boolean down = false;
				boolean left = false;
				boolean right = false;
				for (int k = 0; k < 4; k++) {
					int bool = 0;
					switch (k) {
					case 0:
						if (i + 1 > level.getTiles().length - 1) {
							bool = 1;
							up = true;
						} else if (level.getTiles()[i + 1][j].getTileType() == detection) {
							bool = 1;
							up = true;
						}
						break;
					case 1:
						if (j + 1 > level.getTiles()[0].length - 1) {
							bool = 1;
							right = true;
						} else if (level.getTiles()[i][j + 1].getTileType() == detection) {
							bool = 1;
							right = true;
						}
						break;
					case 2:
						if (i - 1 < 0) {
							bool = 1;
							down = true;
						} else if (level.getTiles()[i - 1][j].getTileType() == detection) {
							bool = 1;
							down = true;
						}
						break;
					case 3:
						if (j - 1 < 0) {
							bool = 1;
							left = true;
						} else if (level.getTiles()[i][j - 1].getTileType() == detection) {
							bool = 1;
							left = true;
						}
						break;
					default:
						break;
					}

					// Add to the bitmask powers of two based on the position of each tile, it will
					// not add if the tile tested is not the same due to setting bool to be 0

					// 1 2 4
					// 128 X 8
					// 62 32 16
					bitmask += Math.pow(2, (k + 1) * 2 - 1) * bool;
				}

				// For the corners, only add the bitmask if there are the two other tiles
				// surrounding it. This reduces the possible values from 256 possibilities to
				// just 47/48 something like that
				for (int k = 0; k < 4; k++) {
					int bool = 0;
					switch (k) {
					case 0:
						if (i + 1 > level.getTiles().length - 1 || j - 1 < 0) {
							bool = 1;
						} else if (level.getTiles()[i + 1][j - 1].getTileType() == detection && up && left) {
							bool = 1;
						}
						break;
					case 1:
						if (i + 1 > level.getTiles().length - 1 || j + 1 > level.getTiles()[0].length - 1) {
							bool = 1;
						} else if (level.getTiles()[i + 1][j + 1].getTileType() == detection && up && right) {
							bool = 1;
						}
						break;
					case 2:
						if (i - 1 < 0 || j + 1 > level.getTiles()[0].length - 1) {
							bool = 1;
						} else if (level.getTiles()[i - 1][j + 1].getTileType() == detection && down && right) {
							bool = 1;
						}
						break;
					case 3:
						if (i - 1 < 0 || j - 1 < 0) {
							bool = 1;
						} else if (level.getTiles()[i - 1][j - 1].getTileType() == detection && down && left) {
							bool = 1;
						}
						break;
					default:
						break;
					}

					bitmask += Math.pow(2, (k * 2)) * bool;
				}

				// Set the bitmask to the tiles tested
				if (level.getTiles()[i][j].getTileType() == detection) {
					level.getTiles()[i][j].setBitmask(bitmask);
				}
			}
		}
	}

	private void generateMap() {
		mapRects = new ArrayList<MapRect>();

		for (int i = 0; i < level.getTiles().length; i++) {
			for (int j = 0; j < level.getTiles()[i].length; j++) {
				if (level.getTiles()[i][j].getTileType() == TILE_TYPE.FLOOR) {
					Tile tile = level.getTiles()[i][j];
					Rectangle rectangle = new Rectangle(tile.getX(), tile.getY(), Tile.TILE_WIDTH, Tile.TILE_WIDTH);

					mapRects.add(new MapRect(rectangle));

				}
			}
		}

		edges = new ArrayList<Edge>();

		for (int i = 0; i < mapRects.size(); i++) {
			for (int j = 0; j < mapRects.get(i).getEdges().length; j++) {
				edges.add(mapRects.get(i).getEdges()[j]);
			}
		}

		Collections.sort(edges);

		for (int i = 0; i < edges.size(); i++) {
			for (int j = 0; j < edges.size(); j++) {
				if (edges.get(i).equalTo(edges.get(j)) && i != j) {
					edges.get(i).removed = true;
					edges.get(j).removed = true;
				}
			}
		}

		for (int i = edges.size() - 1; i >= 0; i--) {
			if (edges.get(i).removed) {
				edges.remove(i);
			}
		}
	}

	private void generateLiquids() {
		generateLakes();
	}

	private void generateLakes() {
		
		ArrayList<Vector2> origins = new ArrayList<Vector2>();
		
		while (origins.size() < 4) {
		
			int randX = (int)(Math.random() * level.getTiles().length);
			int randY = (int)(Math.random() * level.getTiles()[0].length);
			
			if (level.getTiles()[randX][randY].getTileType() == TILE_TYPE.WALL && level.getTiles()[randX][randY].getBitmask() != 255) {
				origins.add(new Vector2(randX, randY));
				level.getTiles()[randX][randY].setTileType(TILE_TYPE.LIQUID);
			}		
		}		
	}

	public Level getLevel() {
		return level;
	}

	public int getTries() {
		return tries;
	}

	public void setTries(int tries) {
		this.tries = tries;
	}

	public int getMaxRoomWidth() {
		return maxRoomWidth;
	}

	public void setMaxRoomWidth(int maxRoomWidth) {
		this.maxRoomWidth = maxRoomWidth;
	}

	public int getMaxRoomHeight() {
		return maxRoomHeight;
	}

	public void setMaxRoomHeight(int maxRoomHeight) {
		this.maxRoomHeight = maxRoomHeight;
	}

	public int getMinRoomWidth() {
		return minRoomWidth;
	}

	public void setMinRoomWidth(int minRoomWidth) {
		this.minRoomWidth = minRoomWidth;
	}

	public int getMinRoomHeight() {
		return minRoomHeight;
	}

	public void setMinRoomHeight(int minRoomHeight) {
		this.minRoomHeight = minRoomHeight;
	}

	public LevelInformation getLevelInfo() {
		return levelInfo;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

}
