package se.exuvo.planets.components;

import se.exuvo.planets.utils.VectorD2;


/**
 * Holds a 2D world-position. Usually for a planet.
 */
public class Position extends VectorD2Component implements Cloneable {

	/**
	 * Creates a new Position at thedefault location (0,0).
	 */
	public Position() {
		super();
	}

	/**
	 * Creates a new Position with specified initial value.
	 * @param initial the initial value of this position.
	 */
	public Position(VectorD2 initial) {
		super(initial);
	}
	
	public Position clone(){
		return new Position(vec.cpy());
	}
}
