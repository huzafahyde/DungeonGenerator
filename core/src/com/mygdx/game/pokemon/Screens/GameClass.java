package com.mygdx.game.pokemon.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameClass extends Game {
	private SpriteBatch batch;

	@Override 
	public void create() {
		batch = new SpriteBatch();
		setScreen(new GameScreen(this));
		//setScreen(new TestScreen(this));
	}

	@Override
	public void render() { 
		super.render();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public void setBatch(SpriteBatch batch) {
		this.batch = batch;
	}
}
