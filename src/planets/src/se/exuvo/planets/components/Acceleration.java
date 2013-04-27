package se.exuvo.planets.components;

import com.badlogic.gdx.math.Vector2;


/**
 * Holds a 2D acceleration-vector. Usually for a planet.
 */
public class Acceleration extends Vector2Component {

	/**
	 * Creates an 0-Acceleration.
	 * The vector is  by default (0,0).
	 */
	public Acceleration() { 
		super();
	}

	/**
	 * Creates a new acceleration with the specified initial value.
	 * @param initial the initial acceleration.
	 */
	public Acceleration(Vector2 initial) { // TODO unnecessary? acceleration is reset in every game-loop, supposedly making this constructor useless.
		super(initial);
	}
}
