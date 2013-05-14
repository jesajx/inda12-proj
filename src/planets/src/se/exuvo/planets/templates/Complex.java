package se.exuvo.planets.templates;

import java.util.Random;

import se.exuvo.planets.EntityFactory;
import se.exuvo.planets.systems.GravitationSystem;
import se.exuvo.planets.utils.VectorD2;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;


public class Complex extends Template {

	private static final int MAX_ORBITALS = 100;
	private static final double MAX_RANGE = 1e5d;
	
	@Override
	public void load(World world) {
		double M = 1e19d, m = 1d;
		double R = 1e3d, r = 100d;
		EntityFactory.createPlanet(world, R, M, new VectorD2(), new VectorD2(), Color.YELLOW).addToWorld();
		
		Random rand = new Random();
		int n = 1+rand.nextInt(MAX_ORBITALS);
		double last_d = R;
		for (int i = 0; i < n; i++) {
			double d = Math.min(last_d+r+rand.nextDouble()*r*1e2, MAX_RANGE);
			last_d = d;
			double v = orbitVelocity(M, d);
			if (rand.nextBoolean()) {
				v = -v;
			}
			Color c = getRandomColor(rand);
			EntityFactory.createPlanet(world, r, m, new VectorD2(d, 0f), new VectorD2(0f, v), c).addToWorld();
		}
	}
	
	private Color getRandomColor(Random rand) {
		return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1f);
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
		return "A sun and 1<n<="+MAX_ORBITALS+"\n" +
				"orbitals at random distances.";
	}

}
