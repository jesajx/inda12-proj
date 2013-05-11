package se.exuvo.planets.templates;

import se.exuvo.planets.EntityFactory;
import se.exuvo.planets.utils.VectorD2;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;

public class Square extends Template {
	private int n = 50;
	@Override
	public void load(World world) {
		int i = 100;
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < n; y++) {
				EntityFactory.createPlanet(world, i, 1e10f, new VectorD2(10*i*x, 10*i*y), new VectorD2(), Color.YELLOW).addToWorld();
			}
		}
	}

	@Override
	public String getDescription() {
		return "A whole bunch of planets placed in\na grid, forming a square.\nThere is " + (n*n) + "planets in this template.";
	}

}
