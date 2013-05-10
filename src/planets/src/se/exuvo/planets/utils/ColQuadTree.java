package se.exuvo.planets.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Velocity;
import se.exuvo.planets.systems.CollisionSystem;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;

/**
 * Tree used to speed up collision detection.
 * A QuadTree node is at any moment in one of three states:
 * stores entities, stores subtree or stores neither.
 */
public class ColQuadTree {
	
	public static int maxDepth = 40;
	
	private Vector2 center;
	private float side; // length of sides in Cube
	private int depth;

	private List<Entity> entities;

    private ColQuadTree[] subs;
    public static final int BL = 0, BR = 1, TL = 2, TR = 3; // {bottom,top}{left,right}
	
	/**
	 * Creates a new QuadTree spanning the given cube.
	 * @param pos
	 *            center of cube
	 * @param side
	 *            length of each side in cube
	 */
	public ColQuadTree(Vector2 pos, float side) {
		this.center = pos;
		this.side = side;
	}
	
	
	public void add(Entity e, Circle vc) {
        if (depth >= maxDepth) {
            if (entities == null) {
                entities = new ArrayList<Entity>();
            }
            entities.add(e);
        } else {
            if (subs == null) {
                addSubtrees();
            }
            boolean[] q = getQuadrants(e, vc);
            for (int i = 0; i < 4; i++) {
                if (q[i]) {
                    subs[i].add(e, vc);
                }
            }
        }
    }

    public int getQuadrant(Vector2 p) {
        if (p.x < center.x) {
            if (p.y < center.y) {
                return BL;
            } else {
                return TL;
            }
        } else {
            if (p.y < center.y) {
                return BR;
            } else {
                return TR;
            }
        }
    }
    
    public boolean[] getQuadrants(Entity e, Circle vc) {
        boolean[] res = new boolean[4];
        if (depth < maxDepth) {
            if (center.cpy().sub(vc.p).len2() < vc.r*vc.r) {
                res[BL] = res[BR] = res[TL] = res[TR] = true;
            } else { // TODO find shorter way
                int q = getQuadrant(vc.p);
                
                if (vc.b.xmin < center.x) {
                    if (vc.b.ymin < center.y && q != TR) {
                        res[BL] = true;
                    }
                    if (vc.b.ymax >= center.y && q != BR) {
                        res[TL] = true;
                    }
                }
                if (vc.b.xmax >= center.x) {
                    if (vc.b.ymin < center.y && q != TL) {
                        res[BR] = true;
                    }
                    if (vc.b.ymax >= center.y && q != BL) {
                        res[TR] = true;
                    }
                }
            }
        }
        return res;
    }

	private void addSubtrees() {
		subs = new ColQuadTree[4];
		
		float subside = side / 2;
        Vector2 k = new Vector2(subside, subside);

		subs[TR] = new ColQuadTree(k.cpy().add(center), subside);
		subs[BL] = new ColQuadTree(k.cpy().mul(-1f).add(center), subside);

        k.set(-subside, subside);

		subs[TL] = new ColQuadTree(k.cpy().add(center), subside);
		subs[BR] = new ColQuadTree(k.cpy().mul(-1f).add(center), subside);
		
        int nextDepth = depth + 1;
        for (ColQuadTree t : subs) {
            t.depth = nextDepth;
        }
	}
	
	public void remove(Entity e) {
	    if (depth >= maxDepth) {
        	if (entities != null) {
	            entities.remove(e);
        	}
        } else if (subs != null) {
		    for (int i = 0; i < 4; i++) {
	    		subs[i].remove(e);
		    }
        }
	}
	public boolean remove(Entity e, Circle vc) {
        if (depth >= maxDepth) {
        	if (entities == null) {
        		return false;
        	}
            return entities.remove(e);
        }
        if (subs == null) {
            return false; // because depth < maxDepth
        }
        boolean[] q = getQuadrants(e, vc);
        boolean success = true;
        for (int i = 0; i < 4; i++) {
        	if (q[i]) {
        		success = subs[i].remove(e, vc) && success;
        	}
        }
        return success;
	}

    public void update(Entity e, Circle old, Circle vc) {
        if (depth < maxDepth && subs != null) {
		    boolean[] q = getQuadrants(e, old);
	        boolean[] qn = getQuadrants(e, vc);
	        for (int i = 0; i < 4; i++) {
	            if (q[i]) {
	                if (qn[i]) {
	                    subs[i].update(e, old, vc);
	                } else {
	                    subs[i].remove(e, old);
	                }
	            } else if (qn[i]) {
	                subs[i].add(e, vc);
	            }
	        }
        }
    }
    
    public void clear() {
    	entities = null;
    	subs = null;
    }
    
    public void update(Map<Entity, Circle> circles) { // TODO make faster
    	clear();
    	for (Entry<Entity, Circle> entry : circles.entrySet()) {
    		Entity e = entry.getKey();
    		Circle vc = entry.getValue();
    		add(e, vc);
    	}
    }
	
	/**
	 * Walk tree to find collisions involving e.
	 */
	public void getCollisions(Entity e, Circle vc, List<Collision> cs, float timeLimit, ComponentMapper<Position> pm, ComponentMapper<Size> sm, ComponentMapper<Velocity> vm) {
        Vector2 p1 = pm.get(e).vec;
        float r1 = sm.get(e).radius;
        if (entities != null) {
            Vector2 v1 = vm.get(e).vec;
            for (Entity e2 : entities) {
                if (e != e2) {
                    Vector2 p2 = pm.get(e2).vec;
                    float r2 = sm.get(e2).radius;
                    Vector2 v2 = vm.get(e2).vec;
                    float t = CollisionSystem.collisionTime(p1, r1, v1, p2, r2, v2);
                    if (!Float.isNaN(t) && t >= 0 && t < timeLimit) {
                        Collision c = new Collision(e, e2, t);
                        cs.add(c);
                    }
                }
            }
        } else if (subs != null) {
        	boolean[] q = getQuadrants(e, vc);
        	for (int i = 0; i < 4; i++) {
        		if (q[i]) {
        			subs[i].getCollisions(e, vc, cs, timeLimit, pm, sm, vm);
        		}
        	}
        }
	}

	/**
	 * Walk tree to find collisions.
     * The same collisions may be detected multiple times,
     * but this is alright because of the way
     * se.exuvo.planets.systems.CollisionSystem works.
	 */
	public void getAllCollisions(List<Collision> cs, float timeLimit, ComponentMapper<Position> pm, ComponentMapper<Size> sm, ComponentMapper<Velocity> vm) {
        if (entities != null) {
            for (int i = 0; i < entities.size(); i++) {
                Entity e1 = entities.get(i);
                Vector2 p1 = pm.get(e1).vec;
                Vector2 v1 = vm.get(e1).vec;
                float r1 = sm.get(e1).radius;
                for (int j = i+1; j < entities.size(); j++) {
                    Entity e2 = entities.get(j);
                    Vector2 p2 = pm.get(e2).vec;
                    Vector2 v2 = vm.get(e2).vec;
                    float r2 = sm.get(e2).radius;
                    float t = CollisionSystem.collisionTime(p1, r1, v1, p2, r2, v2);
                    if (!Float.isNaN(t) && t >= 0 && t < timeLimit) {
                        Collision c = new Collision(e1, e2, t);
                        cs.add(c);
                    }
                }
            }
        } else if (subs != null) {
        	for (ColQuadTree t : subs) {
				t.getAllCollisions(cs, timeLimit, pm, sm, vm);
        	}
		}
	}
}
