package se.exuvo.planets.components;

import com.artemis.Component;

/**
 * Holds a mass. Usually for a planet.
 */
public class Mass extends Component {
	public float mass;

	/**
	 * Creates a new Mass with the intial value set to 0.
	 */
	public Mass() {}

	/**
	 * Creates a mass with the given intial value.
	 * @param initialMass the intial value of this mass.
	 */
	public Mass(float initialMass) {
		mass = initialMass;
	}
}
