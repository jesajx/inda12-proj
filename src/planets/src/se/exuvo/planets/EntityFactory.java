package se.exuvo.planets;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Colour;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Particle;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Velocity;
import se.exuvo.planets.systems.GravitationSystem;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Contains methods for creating Entities of the game, most notably planets.
 */
public class EntityFactory {

	/**
	 * Creates a new planet with the given initial properties. Remember to call {@link Entity#addToWorld()} afterwards.
	 * 
	 * @param world the {@link World} to create the planet in.
	 * @param radius the radius of the planet. Used to set {@link Size}.
	 * @param mass the mass of the planet. Used to set {@link Mass}.
	 * @param pos the 2D-position of the planet. Used to set {@link Position}.
	 * @param vel the 2D-velocity of the planet. Used to set {@link Velocity}.
	 * @param color the color of the planet. used to set {@link Colour}.
	 * @return a reference to the created planet.
	 */
	public static Entity createPlanet(World world, float radius, float mass, Vector2 pos, Vector2 vel, Color color) {
		Entity e = world.createEntity();

		Size s = new Size(radius);
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
		float size = MathUtils.random(1.0f, 10.0f);
		float mass = MathUtils.random(10.0f, 100.0f);
		Vector2 pos = new Vector2(MathUtils.random(100, 300), MathUtils.random(100, 300));
		Vector2 vel = new Vector2();
		Color color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);

		return createPlanet(world, size, mass, pos, vel, color);
	}

	public static Entity createHollowPlanet(World world, float radius, float mass, Vector2 position, Color color) {
		Entity e = world.createEntity();

		e.addComponent(new Position(position));
		e.addComponent(new Size(radius));
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
	public static void fillPlanet(World world, Entity e, Vector2 velocity) {
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
	
    public static Entity createParticleEffect(World world, Vector2 position){
		Entity e = world.createEntity();
		
		e.addComponent(new Position(position));
		e.addComponent(new Particle());
		
		return e;
	}
    
}
