package se.exuvo.planets;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Colour;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Particle;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Radius;
import se.exuvo.planets.components.Velocity;
import se.exuvo.planets.utils.VectorD2;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * Contains methods for creating Entities of the game, most notably planets.
 */
public class EntityFactory {

	/**
	 * Creates a new planet with the given initial properties. Remember to call {@link Entity#addToWorld()} afterwards.
	 * 
	 * @param world the {@link World} to create the planet in.
	 * @param radius the radius of the planet. Used to set {@link Radius}.
	 * @param mass the mass of the planet. Used to set {@link Mass}.
	 * @param pos the 2D-position of the planet. Used to set {@link Position}.
	 * @param vel the 2D-velocity of the planet. Used to set {@link Velocity}.
	 * @param color the color of the planet. used to set {@link Colour}.
	 * @return a reference to the created planet.
	 */
	public static Entity createPlanet(World world, double radius, double mass, VectorD2 pos, VectorD2 vel, Color color) {
		Entity e = world.createEntity();

		Radius s = new Radius(radius);
		e.addComponent(s);

		Mass m = new Mass(mass);
		e.addComponent(m);

		Position position = new Position(pos);
		e.addComponent(position);

		Velocity v = new Velocity(vel);
		e.addComponent(v);

		Acceleration a = new Acceleration();
		e.addComponent(a);

		Colour c = new Colour(color);
		e.addComponent(c);
		
		return e;
	}

	public static Entity createRandomPlanet(World world) {
		double radius = MathUtils.random(1.0f, 10.0f);
		double mass = MathUtils.random(10.0f, 100.0f);
		VectorD2 pos = new VectorD2(MathUtils.random(100, 300), MathUtils.random(100, 300));
		VectorD2 vel = new VectorD2();
		Color color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);

		return createPlanet(world, radius, mass, pos, vel, color);
	}

	public static Entity createHollowPlanet(World world, double radius, double mass, VectorD2 position, Color color) {
		Entity e = world.createEntity();

		e.addComponent(new Position(position));
		e.addComponent(new Radius(radius));
		e.addComponent(new Mass(mass));
		e.addComponent(new Colour(color));

		return e;
	}

	/**
	 * Sets the Velocity of the given Planet and adds an acceleration.
	 * 
	 * @param e the planet.
	 * @param v the velocity to set.
	 */
	public static void fillPlanet(World world, Entity e, VectorD2 velocity) {
		e.addComponent(new Velocity(velocity));
		e.addComponent(new Acceleration());

		e.changedInWorld();
	}
	
	public static Entity createParticleEffect(World world){
		Entity e = world.createEntity();
		
		e.addComponent(new Position());
		e.addComponent(new Particle());
		
		return e;
	}
	
    public static Entity createParticleEffect(World world, VectorD2 position){
		Entity e = world.createEntity();
		
		e.addComponent(new Position(position));
		e.addComponent(new Particle());
		
		return e;
	}
    
}
