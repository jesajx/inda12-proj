package se.exuvo.planets.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * Holds a rgba-color. Usually for a planet.
 */
public class Colour extends Component {
	public Color color;

	public Colour() {}

	public Colour(Color initialColor) {
		color = initialColor;
	}
}
