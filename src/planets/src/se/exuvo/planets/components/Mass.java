package se.exuvo.planets.components;

import com.artemis.Component;

/**
 * Holds a mass. Usually for a planet.
 */
public class Mass extends Component implements Cloneable {
	public double mass;

	/**
	 * Creates a mass with the given initial value.
	 * @param initialMass the initial value of this mass.
	 */
	public Mass(double initialMass) {
		mass = initialMass;
	}
	
	public Mass clone(){
		return new Mass(mass);
	}
}
