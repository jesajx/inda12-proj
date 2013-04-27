package se.exuvo.planets.components;

import com.artemis.Component;

/**
 * Holds the radius of a planet.
 */
public class Size extends Component { // TODO rename to Radius? simple with eclipse.
	public float radius;

	/**
	 * Create a new Size with the intial value set to 0.
	 */
	public Size() {}

	/**
	 * Create a new Size with the given initial value.
	 * @param initialRadius the intial value of this Size.
	 */
	public Size(float initialRadius) {
		radius = initialRadius;
	}
}
