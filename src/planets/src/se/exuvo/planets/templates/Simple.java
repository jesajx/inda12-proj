package se.exuvo.planets.templates;

import se.exuvo.planets.EntityFactory;
import se.exuvo.planets.systems.GravitationSystem;
import se.exuvo.planets.utils.VectorD2;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;

public class Simple extends Template {

	@Override
	public void load(World world) {
		double m1 = 1e19, m2 = 1e4, m3 = 1e4,
				r1 = 2000d, r2 = 500, r3 = 400,
				d1 = 0d, d2 = 1e4d, d3 = 2*d2;
		VectorD2 p1, p2, p3, v1, v2, v3;
		p1 = new VectorD2(d1, 0d);
		p2 = new VectorD2(d2, 0d);
		p3 = new VectorD2(d3, 0d);
		
		v1 = new VectorD2(0d, 0d);
		v2 = new VectorD2(0d, orbitVelocity(m1, d2));
		v3 = new VectorD2(0d, -orbitVelocity(m1, d3));
		
		
		EntityFactory.createPlanet(world, r1, m1, p1, v1, Color.YELLOW).addToWorld();
		EntityFactory.createPlanet(world, r2, m2, p2, v2, Color.WHITE).addToWorld();
		EntityFactory.createPlanet(world, r3, m3, p3, v3, Color.RED).addToWorld();
	}
	
	
	private double orbitVelocity(double mass, double dist) {
		// F = m*a = m*v^2/d = m*d*(2pi/T)^2
		// F = G*m*M/d^2
		// a = G*M/d^2
		// v = sqrt(G*M/d)
		// v = d * 2pi/T
		return Math.sqrt(GravitationSystem.G*mass/dist);
	}

	@Override
	public String getDescription() {
		return "A simple system with a sun\n" +
				"and two orbitals";
	}

}
