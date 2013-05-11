package se.exuvo.planets.templates;

import se.exuvo.planets.EntityFactory;
import se.exuvo.planets.utils.VectorD2;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;

public class BinaryStar extends Template {

	@Override
	public void load(World world) {
		double m = 1e16f;// 1e15f;
		double v = 10f;// 6.f;
		double r = 10 * 6 * 6 * 6;// v*v*v;
		EntityFactory.createPlanet(world, 50f, m, new VectorD2(r, 0), new VectorD2(0, -v), Color.WHITE).addToWorld();
		EntityFactory.createPlanet(world, 50f, m, new VectorD2(-r, 0), new VectorD2(0, v), Color.YELLOW).addToWorld();
	}

	@Override
	public String getDescription() {
		return "A binary starsystem.";
	}

}
