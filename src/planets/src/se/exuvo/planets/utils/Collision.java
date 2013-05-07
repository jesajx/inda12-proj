package se.exuvo.planets.utils;

import com.artemis.Entity;

/**
 * Holds the data of an detected Collision.
 * Involved planets and the time (0 <= t < 1) of collision.
 */
public class Collision implements Comparable<Collision> {
    public float t;
    public final Entity e1, e2; // planets
    
    public Collision(Entity e1, Entity e2, float t) {
        this.e1 = e1;
        this.e2 = e2;
        this.t = t;
    }

	@Override
	public int compareTo(Collision c) {
		return Float.compare(c.t, t); // NOTE: reversed compare
	}
}
