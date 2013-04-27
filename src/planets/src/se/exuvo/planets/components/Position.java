package se.exuvo.planets.components;

import com.badlogic.gdx.math.Vector2;


/**
 * Holds a 2D world-position. Usually for a planet.
 */
public class Position extends Vector2Component {

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
	public Position(Vector2 initial) {
		super(initial);
	}
	
}
