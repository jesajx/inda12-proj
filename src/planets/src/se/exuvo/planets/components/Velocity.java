package se.exuvo.planets.components;

import se.exuvo.planets.utils.VectorD2;

/**
 * Holds a 2D velocity-vector.Usually for a planet.
 */
public class Velocity extends VectorD2Component implements Cloneable {

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
	public Velocity(VectorD2 initial) {
		super(initial);
	}
	
	public Velocity clone(){
		return new Velocity(vec.cpy());
	}
}
