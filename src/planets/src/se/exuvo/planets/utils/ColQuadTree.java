package se.exuvo.planets.utils;

import java.util.ArrayList;
import java.util.List;

import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Velocity;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.FastMath;
import com.badlogic.gdx.math.Vector2;

public class ColQuadTree { // TODO create baseclass to share with GravQuadTree
	
	public static int maxDepth = 1000;
	
	Vector2 pos; // bottom left
	float side; // length of sides in Cube
	int depth;

	ColQuadTree bl, br, tl, tr; // {bottom,top}{left,right}
	List<Entity> entities;
	List<Entity> subentities; // TODO or recurse?
	
	
	/**
	 * Creates a new QuadTree spanning the given cube.
	 * 
	 * @param pos
	 *            bottomLeft corner of cube
	 * @param side
	 *            length of each side in cube
	 */
	public ColQuadTree(Vector2 pos, float side) {
		this.pos = pos;
		this.side = side;
	}
	
	
	
	public void add(Entity e, Bounds b) {
		int q = getIntQuadrant(e, b);
//		System.out.println(e+".add: "+q);
		if (q == 0 || depth >= maxDepth) {
			if (entities == null) {
				entities = new ArrayList<Entity>();
			}
			entities.add(e);
		} else {
			if (bl == null) {
				addSubtrees();
			}
			getQuadrant(q).add(e, b);
			if (subentities == null) {
				subentities = new ArrayList<Entity>();
			}
			subentities.add(e);
		}
	}
	
	private void addSubtrees() {
		float subside = side / 2;

		bl = new ColQuadTree(pos.cpy(), subside);

		br = new ColQuadTree(pos.cpy(), subside);
		br.pos.x += subside;

		tl = new ColQuadTree(pos.cpy(), subside);
		tl.pos.y += subside;

		tr = new ColQuadTree(pos.cpy(), subside);
		tr.pos.x += subside;
		tr.pos.y += subside;
		
		bl.depth = br.depth = tl.depth = tr.depth = this.depth - 1;
	}
	
	public ColQuadTree getQuadrant(int q) {
		switch (q) {
		case 1: return bl;
		case 2: return tl;
		case 3: return br;
		case 4: return tr;
		default: return null;
		}
	}
	
	public ColQuadTree getQuadrant(Entity e, Bounds b) {
		return getQuadrant(getIntQuadrant(e, b));
	}
	public int getIntQuadrant(Entity e, Bounds b) {
//		System.out.println(e+" "+b.xmin+" "+b.xmax+" "+b.ymin+" "+b.ymax);
		float subside = side/2;
		float xmid = pos.x + subside;
		float ymid = pos.y + subside;
//		System.out.println(subside+" "+xmid+" "+ymid);
		if ((b.xmin < xmid && xmid <= b.xmax) || (b.ymin < ymid && ymid <= b.ymax)) { // spans multiple quadrants
			return 0;
		}
		if (b.xmin < xmid) { // ==> xmax < xmid
			if (b.ymin < ymid) {
				return 1; // bl
			} else {
				return 2; // tl
			}
		} else {
			if (b.ymin < ymid) {
				return 3; // br
			} else {
				return 4; // tr
			}
		}
	}
	
	public boolean remove(Entity e, Bounds b) {
		ColQuadTree q = getQuadrant(e, b);
		if (q == null) {
			return entities.remove(e);
		} else {
			return q.remove(e, b);
		}
	}
	
	/**
	 * Walk tree to find collisions involving e.
	 */
	public void getCollisions(Entity e, Bounds b, List<Collision> cs, float timeLimit, ComponentMapper<Position> pm, ComponentMapper<Size> sm, ComponentMapper<Velocity> vm, ComponentMapper<Mass> mm) {
		ColQuadTree q = getQuadrant(e, b);
		if (q == null) {
			Vector2 p1 = pm.get(e).vec;
			float r1 = sm.get(e).radius;
			Vector2 v1 = vm.get(e).vec;
			for (int i = 0; i < entities.size(); i++) {
				Entity e2 = entities.get(i);
				if (e2 != e) {
					Vector2 p2 = pm.get(e2).vec;
					float r2 = sm.get(e2).radius;
					Vector2 v2 = vm.get(e2).vec;
					float t = collisionTime(p1, r1, v1, p2, r2, v2);
					if (!Float.isNaN(t) && t >= 0 && t < timeLimit) {
				        cs.add(new Collision(e, e2, t));
				    }
				}
			}
			if (subentities != null) {
				for (Entity e2 : subentities) { // sublevels
					Vector2 p2 = pm.get(e2).vec;
					float r2 = sm.get(e2).radius;
					Vector2 v2 = vm.get(e2).vec;
					float t = collisionTime(p1, r1, v1, p2, r2, v2);
					if (!Float.isNaN(t) && t >= 0 && t < timeLimit) {
				        cs.add(new Collision(e, e2, t));
				    }
				}
			}
		} else {
			q.getCollisions(e, b, cs, timeLimit, pm, sm, vm, mm);
		}
	}
	
	/**
	 * Walk tree to find collisions.
	 */
	public void getCollisions(List<Collision> cs, float timeLimit, ComponentMapper<Position> pm, ComponentMapper<Size> sm, ComponentMapper<Velocity> vm, ComponentMapper<Mass> mm) {
		if (entities == null && subentities == null) {
			return;
		}
		if (entities != null) {
			for (int i = 0; i < entities.size(); i++) {
				Entity e1 = entities.get(i);
				Vector2 p1 = pm.get(e1).vec;
				float r1 = sm.get(e1).radius;
				Vector2 v1 = vm.get(e1).vec;
				
				
				for (int j = i+1; j < entities.size(); j++) { // same level
					Entity e2 = entities.get(j);
					Vector2 p2 = pm.get(e2).vec;
					float r2 = sm.get(e2).radius;
					Vector2 v2 = vm.get(e2).vec;
					float t = collisionTime(p1, r1, v1, p2, r2, v2);
					if (!Float.isNaN(t) && t >= -1 && t < timeLimit) {
				        cs.add(new Collision(e1, e2, t));
				    }
				}
				if (subentities != null) {
					for (Entity e2 : subentities) { // sublevels
						Vector2 p2 = pm.get(e2).vec;
						float r2 = sm.get(e2).radius;
						Vector2 v2 = vm.get(e2).vec;
						float t = collisionTime(p1, r1, v1, p2, r2, v2);
						if (!Float.isNaN(t) && t >= -1 && t < timeLimit) {
					        cs.add(new Collision(e1, e2, t));
					    }
					}
				}
			}
		}
		
		if (bl != null) {
			bl.getCollisions(cs, timeLimit, pm, sm, vm, mm);
			br.getCollisions(cs, timeLimit, pm, sm, vm, mm);
			tl.getCollisions(cs, timeLimit, pm, sm, vm, mm);
			tr.getCollisions(cs, timeLimit, pm, sm, vm, mm);
		}
	}
	
    private static float collisionTime(Vector2 p1, float r1, Vector2 v1, Vector2 p2, float r2, Vector2 v2) { // O(1)
    	// TODO: http://twobitcoder.blogspot.se/2010/04/circle-collision-detection.html
		// http://stackoverflow.com/questions/7461081/finding-point-of-collision-moving-circles-time
		// http://en.wikipedia.org/wiki/Elastic_collision
		
		// http://stackoverflow.com/questions/6459035/2d-collision-response-between-circles?rq=1
		// t = (||p|| - ||r1+r2||)/||v||
		// where:
		//   p = p1-p2
		//   v = v1-v2

    	Vector2 p = p1.cpy().sub(p2);
		Vector2 v = v1.cpy().sub(v2);
		
		// if planets are already moving away from each other.
	    if (v.dot(p) > 0) {
	        return Float.NaN;
	    }
	    
		// TODO can formula be changed to use len2 instead?
		float pLen = p.len();
	    float vLen = v.len();
	    
	    return (pLen - (r1+r2)) / vLen; // TODO if neg.
    }
    
    public static class Bounds {
    	float xmin, xmax, ymin, ymax;
    	
    	public Bounds(Entity e, ComponentMapper<Position> pm, ComponentMapper<Size> sm, ComponentMapper<Velocity> vm) {
    		Vector2 p = pm.get(e).vec;
			float r = sm.get(e).radius;
			Vector2 v = vm.get(e).vec;
			
			r += (float) FastMath.sqrt(v.len2());
		
			xmin = p.x - r;
			xmax = p.x + r;
			ymin = p.y - r;
			ymax = p.y + r;
    	}
    }
}
