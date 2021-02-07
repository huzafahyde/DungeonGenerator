package com.mygdx.game.pokemon.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.pokemon.Generator.Level;
import com.mygdx.game.pokemon.Generator.LevelGenerator;
import com.mygdx.game.pokemon.Generator.Room;
import com.mygdx.game.pokemon.Logic.Tile;
import com.mygdx.game.pokemon.Logic.Tile.TILE_TYPE;

public class GameScreen implements InputProcessor, Screen {

	private GameClass game;

	private Viewport viewport, mapViewport;
	private OrthographicCamera camera;
	private OrthographicCamera mapCamera;

	private ShapeRenderer sr;
	private ShapeRenderer mapSr;

	private LevelGenerator levelGenerator;

	private boolean cameraUp, cameraDown, cameraRight, cameraLeft, zoomIn, zoomOut;
	private boolean debug;

	private BitmapFont bitmapFont;
	
	private Texture test;
 
	public GameScreen(GameClass game) {
		this.game = game;

		camera = new OrthographicCamera(1920, 1080);
		camera.setToOrtho(false);
		
		mapCamera = new OrthographicCamera(1920, 1080);
		mapCamera.setToOrtho(false);

		// Testing camera position
		camera.position.set(700, 800, 0);
		camera.zoom = 1.55f;
		camera.update();
		
		mapCamera.position.set(600, 600, 0);
		mapCamera.zoom = 2.5f;
		mapCamera.update();
		
		// FitViewport
		viewport = new FitViewport(1920, 1080, camera);
		viewport.apply();
		
		mapViewport = new FitViewport(1920, 1080, mapCamera);
		mapViewport.apply();

		levelGenerator = new LevelGenerator(new Level());

		sr = new ShapeRenderer();
		mapSr = new ShapeRenderer();

		// Test font
		bitmapFont = new BitmapFont();
		
		test = new Texture("test.png");

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render(float delta) {
		//Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_NEAREST); 

		camera.update();
		mapCamera.update();
		
		// SPRITEBATCH

		game.getBatch().begin();
		game.getBatch().setProjectionMatrix(camera.combined);


		game.getBatch().draw(test, 0, 0, 24*50, 24*50);
		
		
		// Draws tiles contained in levelGenerator
		for (int i = 0; i < levelGenerator.getLevel().getTiles().length; i++) {
			for (int j = 0; j < levelGenerator.getLevel().getTiles().length; j++) {
				game.getBatch().draw(levelGenerator.getLevel().getTiles()[i][j].getTexture(),
						levelGenerator.getLevel().getTiles()[i][j].getX(),
						levelGenerator.getLevel().getTiles()[i][j].getY(), Tile.TILE_WIDTH, Tile.TILE_WIDTH);

				// Displays bitmasking values
				if (debug) {
					bitmapFont.draw(game.getBatch(), levelGenerator.getLevel().getTiles()[i][j].getBitmask() + "",
							levelGenerator.getLevel().getTiles()[i][j].getX(),
							levelGenerator.getLevel().getTiles()[i][j].getY() + Tile.TILE_WIDTH);

				}
			}
		}

		// Displays tileset
		if (debug) {
			int x1 = 0;
			for (int i = 0; i < levelGenerator.getLevelInfo().getWallTextureRegions().size(); i++) {
				game.getBatch().draw(levelGenerator.getLevelInfo().getWallTextureRegions().get(i), x1, 0, 24, 24);
				x1 += 24;
			}
		}

		game.getBatch().end();

		// SHAPERENDERER

		
		// Setting up shapeRenderer
		sr.setProjectionMatrix(camera.combined);
		sr.setAutoShapeType(true);
		sr.begin();
		sr.setColor(Color.WHITE);

		// Drawing debug grid 
		if (debug) {
			int x = 0;
			for (int i = 0; i < levelGenerator.getLevel().getTiles().length; i++) {
				int y = 0;
				for (int j = 0; j < levelGenerator.getLevel().getTiles().length; j++) {

					sr.rect(x, y, Tile.TILE_WIDTH, Tile.TILE_WIDTH);

					y += Tile.TILE_WIDTH;
				}
				x += Tile.TILE_WIDTH;
			}
		}

		// Drawing debug room view
		if (debug) {
			for (int i = 0; i < levelGenerator.getLevel().getRooms().size(); i++) {
				Room room = levelGenerator.getLevel().getRooms().get(i);

				if (room.removed) {
					sr.setColor(Color.RED);
				} else {
					sr.setColor(Color.BLACK);
				}
				sr.rect(room.getRect().x, room.getRect().y, room.getRect().width, room.getRect().height);

				sr.setColor(Color.CYAN);
				if (room.beginning)
					sr.setColor(Color.RED);
				if (room.first)
					sr.setColor(Color.BLACK);
				if (room.second)
					sr.setColor(Color.BROWN);
				if (room.last)
					sr.setColor(Color.GREEN);
				Vector2 target = new Vector2(levelGenerator.getLevel().getRooms().get(i).getTarget());
				sr.rect((levelGenerator.getLevel().getRooms().get(i).getX() + target.x) * Tile.TILE_WIDTH,
						(levelGenerator.getLevel().getRooms().get(i).getY() + target.y) * Tile.TILE_WIDTH,
						Tile.TILE_WIDTH, Tile.TILE_WIDTH);
			}
		}
		sr.end();
		
		
		
		mapSr.setProjectionMatrix(mapCamera.combined);
		mapSr.begin(ShapeType.Filled);
		mapSr.setColor(Color.CYAN);

		if (levelGenerator.getEdges() != null) {
			for (int i = 0; i < levelGenerator.getEdges().size(); i++) {
				mapSr.rectLine(levelGenerator.getEdges().get(i).getVertices()[0],
						levelGenerator.getEdges().get(i).getVertices()[1], 4f);
			}
		}
		
		mapSr.end();
		

		moveCamera();
	}

	private void moveCamera() {
		if (cameraUp) {
			camera.position.y += 5f;
		}
		if (cameraDown) {
			camera.position.y -= 5f;
		}
		if (cameraLeft) {
			camera.position.x -= 5f;
		}
		if (cameraRight) {
			camera.position.x += 5f;
		}

		// Limits camera zoom in
		if (zoomIn && camera.zoom >= 0.3f) {
			camera.zoom -= 0.1f;
		}
		if (zoomOut) {
			camera.zoom += 0.1f;
		}

		camera.zoom = Math.round(camera.zoom * 1000.0f) / 1000.0f;
		camera.position.x = Math.round(camera.position.x * 100.0f) / 100.0f;
		camera.position.y = Math.round(camera.position.y * 100.0f) / 100.0f;
		
		//System.out.println(camera.position + ", " + camera.zoom);
		
		camera.update();
	}

	@Override
	public void resize(int width, int height) {		
		viewport.update(width, height);
		mapViewport.update(width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		levelGenerator.getLevelInfo().disposeTileset();
		sr.dispose();
		bitmapFont.dispose();

	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.A)
			cameraLeft = true;
		if (keycode == Input.Keys.D)
			cameraRight = true;
		if (keycode == Input.Keys.W)
			cameraUp = true;
		if (keycode == Input.Keys.S)
			cameraDown = true;
		if (keycode == Input.Keys.Q)
			zoomOut = true;
		if (keycode == Input.Keys.E)
			zoomIn = true;
		if (keycode == Input.Keys.PLUS)
			debug = true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Input.Keys.A)
			cameraLeft = false;
		if (keycode == Input.Keys.D)
			cameraRight = false;
		if (keycode == Input.Keys.W)
			cameraUp = false;
		if (keycode == Input.Keys.S)
			cameraDown = false;
		if (keycode == Input.Keys.Q)
			zoomOut = false;
		if (keycode == Input.Keys.E)
			zoomIn = false;
		if (keycode == Input.Keys.PLUS)
			debug = false;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// Sets default texture
		
		for (int i = 0; i < levelGenerator.getLevel().getTiles().length; i++) {
			for (int j = 0; j < levelGenerator.getLevel().getTiles()[i].length; j++) {
				levelGenerator.getLevel().getTiles()[i][j].setTileType(TILE_TYPE.WALL);
			}
		}

		// Regenerates on click
		levelGenerator.generateLevel();

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
