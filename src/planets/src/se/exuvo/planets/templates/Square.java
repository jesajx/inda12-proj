package se.exuvo.planets.templates;

import se.exuvo.planets.EntityFactory;
import se.exuvo.planets.utils.VectorD2;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;

public class Square extends Template {
	private int side = 30;
	private int padding = 500;
	private double radius = 100d;
	private double mass = 1e10d;
	
	@Override
	public void load(World world) {
		for (int x = 0; x < side; x++) {
			for (int y = 0; y < side; y++) {
				EntityFactory.createPlanet(world, radius, mass, new VectorD2((radius+padding)*x, (radius+padding)*y), new VectorD2(), Color.YELLOW).addToWorld();
			}
		}
	}

	@Override
	public String getDescription() {
		return "A whole bunch of planets\nplaced in a grid,\nforming a square.\nThere are " + (side*side) + " planets\nin this template.";
	}

}
