package se.exuvo.planets;

import se.exuvo.planets.components.Position;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class EntityFactory {
	
	public static Entity createPlanet(World world, float size) {
		Entity e = world.createEntity();
		
		Position position = new Position();
		position.x = 0;
		position.y = 0;
		e.addComponent(position);

		return e;
	}
	

}
