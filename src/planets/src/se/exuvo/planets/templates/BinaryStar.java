package se.exuvo.planets.templates;

import se.exuvo.planets.EntityFactory;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class BinaryStar extends Template {

	@Override
	public void load(World world) {
		float m = 1e16f;// 1e15f;
		float v = 10f;// 6.f;
		float r = 10 * 6 * 6 * 6;// v*v*v;
		EntityFactory.createPlanet(world, 50f, m, new Vector2(r, 0), new Vector2(0, -v), Color.WHITE).addToWorld();
		EntityFactory.createPlanet(world, 50f, m, new Vector2(-r, 0), new Vector2(0, v), Color.YELLOW).addToWorld();
	}

	@Override
	public String getDescription() {
		return "";
	}

}
