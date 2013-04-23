package se.exuvo.planets;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Colour;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Velocity;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class EntityFactory {

	public static Entity createPlanet(World world) {
		float size = MathUtils.random(1.0f, 10.0f);
		float mass = MathUtils.random(10.0f, 100.0f);
		Vector2 pos = new Vector2(MathUtils.random(100, 300), MathUtils.random(100, 300));
		Vector2 vel = new Vector2();
		Color color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);

		return createPlanet(world, size, mass, pos, vel, color);
	}
	
	public static Entity createPlanet(World world, float size, float mass, Vector2 pos, Vector2 vel, Color color) {
		Entity e = world.createEntity();
		
		Size s = new Size(size);
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
	
	public static Entity createHollowPlanet(World world, Position position) {
		Entity e = world.createEntity();
		
		e.addComponent(position);

		Size s = new Size(MathUtils.random(1.0f, 10.0f));
		e.addComponent(s);
		
		Mass m = new Mass(MathUtils.random(10.0f, 100.0f));
		e.addComponent(m);
		
		Colour c = new Colour(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1));
		e.addComponent(c);

		return e;
	}
	
	public static void fillPlanet(Entity e, Velocity v) {
		e.addComponent(v);
		
		Acceleration a = new Acceleration();
		e.addComponent(a);
		
		e.changedInWorld();
	}
	

}
