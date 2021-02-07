package com.mygdx.game.pokemon.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.pokemon.Screens.GameClass;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new GameClass(), config); 
		config.width = 1920;
		config.height = 1080;

		config.x = 0;
		config.y = 0;
	}
}
