package se.exuvo.planets.templates;

import se.exuvo.planets.EntityFactory;
import se.exuvo.planets.utils.VectorD2;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;

public class EarthAndSun extends Template {

	@Override
	public void load(World world) {
		// sun
		float sun_radius = 1.392684e9f / 2 / 1e2f; // m
		float sun_mass = 1.9891e30f / 1e30f; // kg
		EntityFactory.createPlanet(world, sun_radius, sun_mass, new VectorD2(), new VectorD2(), Color.YELLOW).addToWorld();

		// earth
		float earth_radius = 6371e3f / 1e2f; // m
		float earth_mass = 5.9376e24f / 1e30f; // kg
		float earth_aphelion = 152098232e3f;// m // 1.496e8f
		float earth_avg_speed = 29.78e3f; // m/s
		EntityFactory.createPlanet(world, earth_radius, earth_mass, new VectorD2(earth_aphelion, 0), new VectorD2(0, earth_avg_speed),
									Color.BLUE).addToWorld();
	}

	@Override
	public String getDescription() {
		return
			"Star and planet with the\n" +
			"realworld parameters of\n" +
			"the sun and the earth.\n"+
			"It's sort of buggy, but\n" +
			"realistic! It should take\n" +
			"an actual year for the\n" +
			"earth to orbit the sun!\n" +
			"Try it if you have a lot\n" +
			"of time on your hands.";
	}

}
