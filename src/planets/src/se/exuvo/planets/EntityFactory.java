package se.exuvo.planets;

import se.exuvo.planets.components.Position;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class EntityFactory {
	
	public static Entity createPlanet(World world, float size) {
		Entity e = world.createEntity();
		
		Position position = new Position();
		position.vec = new Vector2(MathUtils.random(100, 300), MathUtils.random(100, 300));
		e.addComponent(position);

		return e;
	}
	

}
