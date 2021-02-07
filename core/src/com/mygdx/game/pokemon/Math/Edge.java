package com.mygdx.game.pokemon.Math;

import com.badlogic.gdx.math.Vector2;

public class Edge implements Comparable<Edge> {

	private Vector2 v1, v2;

	public boolean removed;
	
	public Edge(Vector2 v1, Vector2 v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	public Vector2[] getVertices() {
		return new Vector2[] { v1, v2 };
	}

	public boolean equalTo(Edge e2) {
		if ((this.v1.equals(e2.getVertices()[0])&& this.v2.equals(e2.getVertices()[1]))
				|| (this.v1.equals(e2.getVertices()[1]) && this.v2.equals(e2.getVertices()[0]))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(Edge edge) { 
		if (this.v1.y < edge.v1.y) {
			return -1;
		} else if (this.v1.y > edge.v1.y) {
			return 1;
		} else {
			if (this.v1.x < edge.v1.x) {
				return -1;
			} else if (this.v1.x > edge.v1.x) {
				return 1;
			} else {
				if (this.v2.x - this.v1.x != 0 && edge.v2.x - edge.v1.x == 0) {
					return -1;
				} else if (this.v2.x - this.v1.x == 0 && edge.v2.x - edge.v1.x != 0) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}

}
