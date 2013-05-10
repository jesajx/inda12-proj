package se.exuvo.planets.utils;

import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Velocity;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.FastMath;
import com.badlogic.gdx.math.Vector2;

public class Bounds {
	float xmin, xmax, ymin, ymax;
    	
	public Bounds(Vector2 p, float r) {
		xmin = p.x - r;
		xmax = p.x + r;
		ymin = p.y - r;
		ymax = p.y + r;
	}
}
