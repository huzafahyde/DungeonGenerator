package com.mygdx.game.pokemon.Math;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MapRect {

	private Vector2 v1, v2, v3, v4;
	
	private Edge e1, e2, e3, e4; 

	public MapRect(Rectangle rect) {
		generateVertices(rect);
		generateEdges();
	}

	private void generateVertices(Rectangle rect) {
		v1 = new Vector2(rect.x, rect.y);
		v2 = new Vector2(rect.x, rect.y + rect.height);
		v3 = new Vector2(rect.x + rect.width, rect.y + rect.height);
		v4 = new Vector2(rect.x + rect.width, rect.y);
		
	}
	
	private void generateEdges() {
		e1 = new Edge(v1, v2);
		e2 = new Edge(v2, v3);
		e3 = new Edge(v4, v3);
		e4 = new Edge(v1, v4);
	}

	public Vector2[] getVertices() {
		return new Vector2[] { v1, v2, v3, v4 };
	}

	public Edge[] getEdges() {
		return new Edge[] {e1, e2, e3, e4};
	}
}
