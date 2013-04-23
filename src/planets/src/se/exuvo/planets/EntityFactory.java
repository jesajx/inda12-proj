package se.exuvo.planets;

import se.exuvo.planets.components.Colour;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class EntityFactory {
	
	public static Entity createPlanet(World world, float size) {
		Entity e = world.createEntity();
		
		Position position = new Position(new Vector2(MathUtils.random(100, 300), MathUtils.random(100, 300)));
		e.addComponent(position);
		
		Size s = new Size(MathUtils.random(1.0f, 10.0f));
		e.addComponent(s);
		
		Colour c = new Colour(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1));
		e.addComponent(c);

		return e;
	}
	

}
