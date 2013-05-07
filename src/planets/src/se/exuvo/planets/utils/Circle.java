package se.exuvo.planets.utils;

import java.util.ArrayList;
import java.util.List;

import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Velocity;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.FastMath;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Circle {
	Vector2 pos;
	float r;
	
	Entity entity;
	List<Circle> circles;
	boolean overlaps;
	boolean fits;
	
	public Circle(Vector2 pos, float r, Entity e) {
		this.pos = pos;
		this.r = r;
		this.entity = e;
	}
	
	public Circle(Circle a, Circle b) {
		circles = new ArrayList<Circle>();
		circles.add(a);
		circles.add(b);
		fits = true;
	}

	public boolean fits(Circle c) {
		float k = r-c.r; // max dist from this.pos
		return k > 0 && c.pos.cpy().sub(pos).len2() < k*k;
	}
	
	public int getFit(Circle c) {
		float k = r-c.r; // max dist from this.pos
		if (c.pos.cpy().sub(pos).len2() < k*k) {
			if (k > 0) {
				return 1; // this > c
			} else if (k < 0) {
				return -1; // this < c
			} else {
				return 0; // this == c
			}
		} else {
			return -2; // no fit
		}
	}
	
	public boolean overlaps(Circle c) {
		float k = r+c.r; // max dist from this.pos
		return c.pos.cpy().sub(pos).len2() < k*k;
	}
	
	private Circle add(Circle c) {
		if (circles == null) {
			circles = new ArrayList<Circle>();
			circles.add(c);
		} else {
			int overlapNo = 0;
			for (int i = 0; i < circles.size(); i++) {
				Circle k = circles.get(i);
				// TODO
			}
		}
		
		return null;
	}
	
	public static Circle resolve(Circle a, Circle b) { // TODO
		if (a.overlaps(b)) {
			int fit = a.getFit(b);
			if (fit == -2) {
				return a.add(b);
			} else {
				if (fit >= 0) {
					return a.add(b);
				} else {
					return b.add(a);
				}
			}
		} else {
			return new Circle(a,b);
		}
	}
	
	
	
	public static Circle make(Entity e, ComponentMapper<Position> pm, ComponentMapper<Size> sm, ComponentMapper<Velocity> vm) {
		Vector2 p = pm.get(e).vec;
		float r = sm.get(e).radius;
		Vector2 v = vm.get(e).vec;
		r += (float) FastMath.sqrt(v.len2());
		return new Circle(p, r, e);
	}
	
	public static Circle make(ImmutableBag<Entity> entities, ComponentMapper<Position> pm, ComponentMapper<Size> sm, ComponentMapper<Velocity> vm) {
		if (entities.size() == 0) {
			return null; // TODO or new Circle(new Vector2(), 0f) ?
		}
		Circle res = make(entities.get(0), pm, sm, vm);
		for (int i = 1; i < entities.size(); i++) {
			Entity e = entities.get(i);
			Circle c = make(e, pm, sm, vm);
			res = resolve(res, c);
		}
		return res;
	}
}
