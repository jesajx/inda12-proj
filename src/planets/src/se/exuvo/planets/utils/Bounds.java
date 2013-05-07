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
	
	public static Bounds velCircle(Entity e, ComponentMapper<Position> pm, ComponentMapper<Size> sm, ComponentMapper<Velocity> vm) {
		Vector2 p = pm.get(e).vec;
		float r = sm.get(e).radius;
		Vector2 v = vm.get(e).vec;
		
		r += (float) FastMath.sqrt(v.len2());
		
		return new Bounds(p, r);
	}
}
