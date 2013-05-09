package se.exuvo.planets.templates;

import se.exuvo.planets.EntityFactory;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Square extends Template {

	@Override
	public void load(World world) {
		int n = 50;
		int i = 100;
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < n; y++) {
				EntityFactory.createPlanet(world, i, 1e10f, new Vector2(10*i*x, 10*i*y), new Vector2(), Color.YELLOW).addToWorld();
			}
		}
	}

	@Override
	public String getDescription() {
		return "";
	}

}
