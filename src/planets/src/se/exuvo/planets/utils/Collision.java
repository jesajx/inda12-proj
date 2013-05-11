package se.exuvo.planets.utils;

import com.artemis.Entity;

/**
 * Holds the data of an detected Collision.
 * Involved planets and the time (0 <= t < 1) of collision.
 */
public class Collision implements Comparable<Collision> {
    public double t;
    public final Entity e1, e2; // planets
    
    public Collision(Entity e1, Entity e2, double t2) {
        this.e1 = e1;
        this.e2 = e2;
        this.t = t2;
    }

	@Override
	public int compareTo(Collision c) {
		return Double.compare(c.t, t); // NOTE: reversed compare
	}
}
