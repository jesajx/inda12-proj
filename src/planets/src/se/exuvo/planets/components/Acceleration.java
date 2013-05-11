package se.exuvo.planets.components;

import se.exuvo.planets.utils.VectorD2;


/**
 * Holds a 2D acceleration-vector. Usually for a planet.
 */
public class Acceleration extends VectorD2Component implements Cloneable {

	/**
	 * Creates a new Acceleration set to (0,0).
	 */
	public Acceleration() { 
		super();
	}

	/**
	 * Creates a new acceleration with the specified initial value.
	 * @param initial the initial acceleration.
	 */
	public Acceleration(VectorD2 initial) { // TODO unnecessary? acceleration is reset in every game-loop, supposedly making this constructor useless.
		super(initial);
	}
	
	public Acceleration clone(){
		return new Acceleration(vec.cpy());
	}
}
