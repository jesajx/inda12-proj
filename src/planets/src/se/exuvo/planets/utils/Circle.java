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
	List<Entity> subentities;
	List<Circle> circles;
	
	public Circle(Vector2 pos, float r, Entity e) {
		this.pos = pos;
		this.r = r;
		this.entity = e;
	}
	
	public Circle(Circle a, Circle b) {
        Vector2 p = b.pos.cpy().sub(a.pos);
        this.r = a.r + p.len() + b.r;
        this.pos = p.div(2).add(b.pos);

		circles = new ArrayList<Circle>();
		circles.add(a);
		circles.add(b);

        subentities = new ArrayList<Entity>();
        if (a.entity != null) {
            subentities.add(a.entity);
        }
        if (a.subentities != null) {
            subentities.add(a.subentities);
        }

        if (b.entity != null) {
            subentities.add(b.entity);
        }
        if (b.subentities != null) {
            subentities.add(b.subentities);
        }
	}

    /**
     * Checks if this Circle completely encapsules c.
     */
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
	
    /** Assumes this.fits(c) */
	private void add(Circle c) {
        if (subentities == null) {
            subentities = new ArrayList<Entity>();
        }
        if (c.entity != null) {
            subentities.add(c.entity);
        }
        if (c.subentities != null) {
            subentities.addAll(c.subentities);
        }

		if (circles == null) {
			circles = new ArrayList<Circle>();
		} else {
			for (int i = 0; i < circles.size(); i++) {
				Circle k = circles.get(i);
                if (k.fits(c)) {
                    k.add(c);
                    return;
                }
			}
		}
        circles.add(c);
	}
	
	public static Circle resolve(Circle a, Circle b) { // TODO
        int fit = a.getFit(b);
        if (fit == -2) {
            return new Circle(a, b);
        } else if (fit >= 0) {
            a.add(b);
            return a;
        } else {
            b.add(a);
            return b;
        }
	}
	
    public void getAllCollisions(List<Collision> cs) {
        for (int i = 0; i < circles.size(); i++) {
            Circle a = circles.get(i);
            a.getAllCollisions(cs);
            if (entity != null) {
                if (a.entity != null) {
                    getCollision(entity, a.entity, cs);
                }
                if (a.subentities != null) {
                    getCollisions(e, a.subentities, cs);
                }
            }
            for (int j = i+1; j < circles.size(); j++) {
                Circle b = circles.get(j);
                if (a.entity != null) {
                    if (b.entity != null) {
                        getCollision(a.entity, b.entity, cs);
                    }
                    if (b.subentities != null) {
                        getCollision(a.entity, b.subentities, cs);
                    }
                }
            }
        }

    }


    public static void getCollision(Entity e, List<Entity> entities, List<Collision> cs) {
        for (Entity e2 : entities) {
            getCollision(e, e2, cs);
        }
    }

    public static void getCollision(Entity e1, Entity e2, List<Collision> cs) {
        float t = CollisionSystem.collisionTime(e1, e2);
        if (!Float.isNaN(t) && t >= 0 && t < timeLimit) {
            Collsision c = new Collision(e1, e2, t);
            cs.add(c);
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
			return new Circle(new Vector2(), 0f);
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
