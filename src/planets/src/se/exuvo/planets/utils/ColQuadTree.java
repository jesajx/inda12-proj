package se.exuvo.planets.utils;

import java.util.ArrayList;
import java.util.List;

import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Radius;
import se.exuvo.planets.components.Velocity;
import se.exuvo.planets.systems.CollisionSystem;

import com.artemis.ComponentMapper;
import com.artemis.Entity;

/**
 * Tree used to speed up collision detection.
 * A QuadTree node is at any moment in one of three states:
 * stores entities, stores subtree or stores neither.
 */
public class ColQuadTree {
	
	public static int maxDepth = 240;
	
	private VectorD2 center;
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
	public ColQuadTree(VectorD2 pos, double side) {
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
	        		if (subs != null) {
		        		if (subs[qn] == null) {
				        	addSubtree(qn);
				        }
		        		subs[qn].add(e, vc);
	        		}
	        	}
	        } else {
	        	
	        	if (qn == -1) {
	        		if (entities == null) {
        				entities = new ArrayList<Entity>();
        			}
    				entities.add(e);
	        	} else {
        			if (subs == null) {
        				subs = new ColQuadTree[4];
        			}
        			if (subs[qn] == null) {
			        	addSubtree(qn);
			        }
        			if (qn == q) {
        				subs[qn].update(e, old, vc);
        			} else {
        				if (subs != null && subs[q] != null) {
        					subs[q].remove(e, old);
        				}
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


    
    
    public int getQuadrant(VectorD2 p) {
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
		double s = side / 2;
		switch (tree) {
		case BL: subs[BL] = new ColQuadTree(new VectorD2(-s, -s).add(center), s); break;
		case BR: subs[BR] = new ColQuadTree(new VectorD2(s, -s).add(center), s); break;
		case TL: subs[TL] = new ColQuadTree(new VectorD2(-s, s).add(center), s); break;
		case TR: subs[TR] = new ColQuadTree(new VectorD2(s, s).add(center), s); break;
		default: throw new RuntimeException();
		}
		subs[tree].depth = depth + 1;
	}
	
	
	
	/**
	 * Walk tree to find collisions involving e.
	 */
	public void getCollisions(Entity e, Circle vc, List<Collision> cs, double timeLimit, ComponentMapper<Position> pm, ComponentMapper<Radius> rm, ComponentMapper<Velocity> vm) {
        if (subs != null) {
	    	int q = getQuadrant(e, vc);
        	if (q == -1) {
        		VectorD2 p1 = pm.get(e).vec;
		        double r1 = rm.get(e).radius;
	            VectorD2 v1 = vm.get(e).vec;
	            
	            for (Entity e2 : entities) {
                    VectorD2 p2 = pm.get(e2).vec;
                    double r2 = rm.get(e2).radius;
                    VectorD2 v2 = vm.get(e2).vec;
                    double t = CollisionSystem.collisionTime(p1, r1, v1, p2, r2, v2);
                    if (!Double.isNaN(t) && t >= 0 && t < timeLimit) {
                        Collision c = new Collision(e, e2, t);
                        if (!cs.contains(c)) {
	                        cs.add(c);
                        }
                    }
	            }
	            
	            for (Entity e2 : subentities) {
                    VectorD2 p2 = pm.get(e2).vec;
                    double r2 = rm.get(e2).radius;
                    VectorD2 v2 = vm.get(e2).vec;
                    double t = CollisionSystem.collisionTime(p1, r1, v1, p2, r2, v2);
                    if (!Double.isNaN(t) && t >= 0 && t < timeLimit) {
                        Collision c = new Collision(e, e2, t);
                        cs.add(c);
                    }
	            }
        	} else {
        		if (subs[q] != null) {
        			subs[q].getCollisions(e, vc, cs, timeLimit, pm, rm, vm);
        		}
        	}
        }
	}

	
	/**
	 * Walk tree to find collisions.
	 */
	public void getAllCollisions(List<Collision> cs, double timeLimit, ComponentMapper<Position> pm, ComponentMapper<Radius> sm, ComponentMapper<Velocity> vm) {
        if (entities != null) {
            for (int i = 0; i < entities.size(); i++) {
                Entity e1 = entities.get(i);
                VectorD2 p1 = pm.get(e1).vec;
                VectorD2 v1 = vm.get(e1).vec;
                double r1 = sm.get(e1).radius;
                
                for (int j = i+1; j < entities.size(); j++) {
                    Entity e2 = entities.get(j);
                    VectorD2 p2 = pm.get(e2).vec;
                    VectorD2 v2 = vm.get(e2).vec;
                    double r2 = sm.get(e2).radius;
                    double t = CollisionSystem.collisionTime(p1, r1, v1, p2, r2, v2);
                    if (!Double.isNaN(t) && t >= 0 && t < timeLimit) {
                        Collision c = new Collision(e1, e2, t);
                        if (!cs.contains(c)) {
	                        cs.add(c);
                        }
                    }
                }
                
                for (Entity e2 : subentities) {
                    VectorD2 p2 = pm.get(e2).vec;
                    VectorD2 v2 = vm.get(e2).vec;
                    double r2 = sm.get(e2).radius;
                    double t = CollisionSystem.collisionTime(p1, r1, v1, p2, r2, v2);
                    if (!Double.isNaN(t) && t >= 0 && t < timeLimit) {
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
