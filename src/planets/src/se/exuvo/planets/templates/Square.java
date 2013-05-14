package se.exuvo.planets.templates;

import se.exuvo.planets.EntityFactory;
import se.exuvo.planets.utils.VectorD2;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;

public class Square extends Template {
	private int side = 30;
	private int padding = 500;
	private double radius = 100d;
	private double mass = 1e1d;
	
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
		return "A whole bunch of planets\n" +
				"placed in a grid-like pattern.\n" +
				"There are " + (side*side) + " planets\n" +
				"in this template - exceptionally good\n" +
				"at causing collision-lagg.\n" +
				"Also see what happens when\n"+
				"collisions are turned off\n" +
				"and some planets gain mass!";
	}

}
