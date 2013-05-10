package se.exuvo.planets.utils;

import java.util.ArrayList;
import java.util.List;

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
	
	public static int maxDepth = 240;
	
	private Vector2 center;
	private double side;
	private int depth;

	private List<Entity> entities;
	private List<Entity> subentities;

    private ColQuadTree[] subs;
    public static final int BL = 0, BR = 1, TL = 2, TR = 3; // {bottom,top}{left,right}
    
	
	/**
	 * Creates a new QuadTree spanning the given cube.
	 * @param pos
	 *            center of cube
	 * @param side
	 *            length of each side in cube
	 */
	public ColQuadTree(Vector2 pos, double side) {
		this.center = pos;
		this.side = side;
	}
	
	
	
	public void add(Entity e, Circle vc) {
		int q = getQuadrant(e, vc);
        if (depth >= maxDepth || q == -1) {
            if (entities == null) {
                entities = new ArrayList<Entity>();
            }
            entities.add(e);
        } else {
    		if (subentities == null) {
                subentities = new ArrayList<Entity>();
            }
            subentities.add(e);
        	if (subs == null) {
        		subs = new ColQuadTree[4];
        	}
        	if (subs[q] == null) {
        		addSubtree(q);
        	}
        	subs[q].add(e, vc);
        }
    }
	
    public void update(Entity e, Circle old, Circle vc) {
        if (depth < maxDepth) {
		    int q = getQuadrant(e, old);
	        int qn = getQuadrant(e, vc);
	        
	        if (q == -1) {
	        	if (qn != -1) {
	        		if (entities != null) {
		        		entities.remove(e);
	        		}
	        		subs[qn].add(e, vc);
	        	}
	        } else {
	        	if (q == qn) {
	        		subs[q].update(e, old, vc);
	        	} else {
	        		subs[q].remove(e, vc);
	        		if (qn == -1) {
	        			if (entities == null) {
	        				entities = new ArrayList<Entity>();
	        			}
        				entities.add(e);
	        		} else {
		        		subs[qn].add(e, vc);
	        		}
	        	}
	        }
        }
    }
    
    
    
    
	public void remove(Entity e) {
    	if (entities != null) {
            entities.remove(e);
        }
    	if (subs != null) {
		    for (int i = 0; i < 4; i++) {
		    	if (subs[i] != null) {
		    		subs[i].remove(e);
		    	}
		    }
        }
	}
	public void remove(Entity e, Circle vc) {
		int q = getQuadrant(e, vc);
        if (depth >= maxDepth || q == -1) {
        	if (entities != null) {
	            entities.remove(e);
        	}
        } else if (subs != null && subs[q] != null) {
        	subs[q].remove(e, vc);
        }
	}

	
    public void clear() {
    	entities = null;
    	subs = null;
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
    
    public int getQuadrant(Entity e, Circle vc) {
        if (depth < maxDepth) {
        	if (vc.b.xmax < center.x) {
        		if (vc.b.ymax < center.y) {
        			return BL;
        		} else if (vc.b.ymin >= center.y) {
        			return TL;
        		}
        	} else if (vc.b.xmin >= center.x) {
				if (vc.b.ymax < center.y) {
					return BR;
        		} else if (vc.b.ymin >= center.y) {
        			return TR;
        		}
        	}
        }
        return -1;
    }
    
    
	private void addSubtree(int tree) {
		float s = (float) (side / 2);
		switch (tree) {
		case BL: subs[BL] = new ColQuadTree(new Vector2(-s, -s).add(center), s); break;
		case BR: subs[BR] = new ColQuadTree(new Vector2(s, -s).add(center), s); break;
		case TL: subs[TL] = new ColQuadTree(new Vector2(-s, s).add(center), s); break;
		case TR: subs[TR] = new ColQuadTree(new Vector2(s, s).add(center), s); break;
		default: throw new RuntimeException();
		}
		subs[tree].depth = depth + 1;
	}
	
	
	
	/**
	 * Walk tree to find collisions involving e.
	 */
	public void getCollisions(Entity e, Circle vc, List<Collision> cs, float timeLimit, ComponentMapper<Position> pm, ComponentMapper<Size> sm, ComponentMapper<Velocity> vm) {
        if (subs != null) {
	    	int q = getQuadrant(e, vc);
        	if (q == -1) {
        		Vector2 p1 = pm.get(e).vec;
		        float r1 = sm.get(e).radius;
	            Vector2 v1 = vm.get(e).vec;
	            for (Entity e2 : subentities) {
                    Vector2 p2 = pm.get(e2).vec;
                    float r2 = sm.get(e2).radius;
                    Vector2 v2 = vm.get(e2).vec;
                    float t = CollisionSystem.collisionTime(p1, r1, v1, p2, r2, v2);
                    if (!Float.isNaN(t) && t >= 0 && t < timeLimit) {
                        Collision c = new Collision(e, e2, t);
                        cs.add(c);
                    }
	            }
        	} else {
        		if (subs[q] != null) {
        			subs[q].getCollisions(e, vc, cs, timeLimit, pm, sm, vm);
        		}
        	}
        }
	}

	
	/**
	 * Walk tree to find collisions.
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
        		if (t != null) {
					t.getAllCollisions(cs, timeLimit, pm, sm, vm);
        		}
        	}
		}
	}
}
