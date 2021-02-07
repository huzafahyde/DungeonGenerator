package com.mygdx.game.pokemon.Logic;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class AnimatedTile {

	private Texture texture, palette;

	private int counter = 0;

	private HashMap<Vector2, Integer> colorPalleteHashMap;

	public HashMap<Vector2, Integer> getColorPalleteHashMap() {
		return colorPalleteHashMap;
	}

	private Color[][] colors;

	public AnimatedTile(Texture texture, Texture palette) {
		this.texture = texture;
		this.palette = palette;

		getColors();

		/*
		 * for (int i = 0; i < colors.length; i++) { for (int j = 0; j <
		 * colors[i].length; j++) { //System.out.println(colors[i][j]); } }
		 */

		setColors();
		regenerateTexture();
	}

	private void getColors() {
		int rows = palette.getHeight() / 11;
		int cols = palette.getWidth() / 11;

		colors = new Color[rows][cols];

		Pixmap pixmap = textureToPixmap(palette);

		int y = 3;
		for (int i = 0; i < colors.length; i++) {
			int x = 3;
			for (int j = 0; j < colors[i].length; j++) {
				Color color = new Color(pixmap.getPixel(x, y));
				colors[i][j] = color;
				x += 11;
			}
			y += 11;
		}
	}

	private void setColors() {
		Pixmap pixmap = textureToPixmap(texture);
		colorPalleteHashMap = new HashMap<Vector2, Integer>();
		
		System.out.println(colors.length);

		for (int i = 0; i < pixmap.getWidth(); i++) {
			for (int j = 0; j < pixmap.getHeight(); j++) {
				for (int k = 0; k < colors[0].length; k++) {
					if (pixmap.getPixel(i, j) == Color.rgba8888(colors[0][k])
							&& pixmap.getPixel(i, j) != Color.rgba8888(1, 0, 1, 1)) {
						colorPalleteHashMap.put(new Vector2(i, j), k);
						// System.out.println("(" + i + ", " + j + "), " + k);
					} 
				}
			}
		}

		// System.out.println(colorPalleteHashMap);

	}

	public void regenerateTexture() {
		Pixmap pixmap = textureToPixmap(texture);

		for (int i = 0; i < pixmap.getWidth(); i++) {
			for (int j = 0; j < pixmap.getHeight(); j++) {
				pixmap.setBlending(Pixmap.Blending.None);
				if (pixmap.getPixel(i, j) == Color.rgba8888(1, 0, 1, 1)) {
					pixmap.drawPixel(i, j, 0x00000000);
					pixmap.setBlending(Pixmap.Blending.SourceOver);
				} else {
					if (colorPalleteHashMap.get(new Vector2(i, j)) != null) {
						if (counter + 1 < colors.length) {
							pixmap.drawPixel(i, j,
									Color.rgba8888(colors[counter + 1][colorPalleteHashMap.get(new Vector2(i, j))]));
						} else {
							pixmap.drawPixel(i, j,
									Color.rgba8888(colors[0][colorPalleteHashMap.get(new Vector2(i, j))]));
						}
					}
				}
			}
		}
		
		counter++;
		if (counter == colors.length) {
			counter = 0;
		}

		texture = new Texture(pixmap);

	}

	private Pixmap textureToPixmap(Texture texture) {
		if (!texture.getTextureData().isPrepared()) {
			texture.getTextureData().prepare();
		}

		return texture.getTextureData().consumePixmap();
	}

	public Texture getTexture() {
		return texture;
	}

}
