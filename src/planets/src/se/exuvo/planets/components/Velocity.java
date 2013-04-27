package se.exuvo.planets.components;

import com.badlogic.gdx.math.Vector2;

/**
 * Holds a 2D velocity-vector.Usually for a planet.
 */
public class Velocity extends Vector2Component {

	/**
	 * Creates a new Velocity initially (0,0).
	 */
	public Velocity() {
		super();
	}
	/**
	 * Creates a new Velocity with the given initial value.
	 * @param initial the initial vector of this velocity.
	 */
	public Velocity(Vector2 initial) {
		super(initial);
	}
}
