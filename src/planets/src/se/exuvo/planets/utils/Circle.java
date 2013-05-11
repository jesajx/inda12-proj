package se.exuvo.planets.utils;


public class Circle {
	
	public VectorD2 p;
	public double r;
	public Bounds b;
	
	public Circle(VectorD2 p, double r) {
		this.p = p;
		this.r = r;
		this.b = new Bounds(p, r);
	}
	
	@Override
	public String toString() {
		return "[" + p.toString() + " " + r + "]";
	}
}
