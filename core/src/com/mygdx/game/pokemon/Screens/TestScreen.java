package com.mygdx.game.pokemon.Screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.pokemon.Logic.AnimatedTile;

public class TestScreen implements InputProcessor, Screen {

	private GameClass game;

	Texture tile, palette, sparkle, sparklePallete;

	AnimatedTile animatedTile, sparkleTile;
	
	BitmapFont font;
	
	public TestScreen(GameClass game) {
		this.game = game;

	}

	@Override
	public void show() {

		tile = new Texture("Tilesets/water_1.png");
		palette = new Texture("Tilesets/test2.png");
		sparkle = new Texture("Tilesets/sparkles_1.png");
		sparklePallete = new Texture("Tilesets/sparklePallete.png");
		
		animatedTile = new AnimatedTile(tile, palette);
		sparkleTile = new AnimatedTile(sparkle, sparklePallete);
		
		font = new BitmapFont();
		
	}
	
	int tileFrames = 0, sparkleFrames = 0;
	 
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		tileFrames ++;
		sparkleFrames ++;
		
		if (tileFrames > 20) {
			tileFrames = 0;
			animatedTile.regenerateTexture();
		}
		
		if (sparkleFrames > 13) {
			sparkleFrames = 0;
			sparkleTile.regenerateTexture();
		}
		
		game.getBatch().begin();
		game.getBatch().draw(sparkleTile.getTexture(), 0, 0, sparkleTile.getTexture().getWidth() * 10, sparkleTile.getTexture().getHeight()* 10);
		game.getBatch().draw(animatedTile.getTexture(), 0, 0, animatedTile.getTexture().getWidth()* 10, animatedTile.getTexture().getHeight()* 10);

		/*
		for (int i = 0; i < sparkleTile.getTexture().getWidth(); i++) {
			for (int j = 0; j < sparkleTile.getTexture().getHeight(); j++) {
				font.draw(game.getBatch(), "" + sparkleTile.getColorPalleteHashMap().get(new Vector2(i, j)), i * 45, (sparkleTile.getTexture().getHeight() - j) * 45);
			}
		}
		//for (int i = 0; i < animatedTile.getTexture().getWidth(); i++) {
		//	for (int j = 0; j < animatedTile.getTexture().getHeight(); j++) {
		//		font.draw(game.getBatch(), "" + animatedTile.getColorPalleteHashMap().get(new Vector2(i, j)), i  * 45, 50 + (animatedTile.getTexture().getHeight() -j) * 45);
		//	}
		//}*/
		
		game.getBatch().end();
		
		System.out.println(delta);

	}

	private Pixmap textureToPixmap(Texture texture) {
		if (!texture.getTextureData().isPrepared()) {
			texture.getTextureData().prepare();
		}

		return texture.getTextureData().consumePixmap();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
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
