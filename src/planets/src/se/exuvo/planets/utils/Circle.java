package se.exuvo.planets.utils;

import com.badlogic.gdx.math.Vector2;

public class Circle {
	
	public Vector2 p;
	public float r;
	public Bounds b;
	
	public Circle(Vector2 p, float r) {
		this.p = p;
		this.r = r;
		this.b = new Bounds(p, r);
	}
	
	@Override
	public String toString() {
		return "[" + p.toString() + " " + r + "]";
	}
}
