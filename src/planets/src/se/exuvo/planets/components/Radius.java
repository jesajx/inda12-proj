package se.exuvo.planets.components;

import com.artemis.Component;

/**
 * Holds the radius of a planet.
 */
public class Radius extends Component { // TODO rename to Radius? simple with eclipse.
	public double radius;

	/**
	 * Create a new Size with the given initial value.
	 * @param initialRadius the initial value of this Size.
	 */
	public Radius(double initialRadius) {
		radius = initialRadius;
	}
}
