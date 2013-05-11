package se.exuvo.planets.utils;

public class Bounds {
	double xmin, xmax, ymin, ymax;
    	
	public Bounds(VectorD2 p, double r) {
		xmin = p.x - r;
		xmax = p.x + r;
		ymin = p.y - r;
		ymax = p.y + r;
	}
}
