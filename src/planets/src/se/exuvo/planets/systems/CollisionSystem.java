package se.exuvo.planets.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Radius;
import se.exuvo.planets.components.Velocity;
import se.exuvo.planets.utils.Circle;
import se.exuvo.planets.utils.ColQuadTree;
import se.exuvo.planets.utils.Collision;
import se.exuvo.planets.utils.VectorD2;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.FastMath;
import com.artemis.utils.ImmutableBag;

/**
 * System responsible for checking for and handling collisions between planets.
 */
public class CollisionSystem extends IntervalEntitySystem {

	@Mapper ComponentMapper<Position> pm;
	@Mapper ComponentMapper<Radius> rm;
	@Mapper ComponentMapper<Velocity> vm;
	@Mapper ComponentMapper<Mass> mm;
	
        
	public static double MIN_SIDE = 1e1f;
    public static double TREE_SIDE =Double.MAX_VALUE;// MIN_SIDE * Math.pow(2, ColQuadTree.maxDepth); // TODO
	ColQuadTree tree = new ColQuadTree(new VectorD2(), TREE_SIDE);
	Map<Entity, Circle> circles = new HashMap<Entity, Circle>();
	
	
	/** Used to check if the game is paused. */
	private InputSystem insys;
	private AudioSystem ausys;
	
	
    public CollisionSystem() {
        super(Aspect.getAspectForAll(Position.class, Radius.class, Velocity.class, Mass.class, Acceleration.class), Settings.getFloat("PhysicsStep"));
    }
    
    @Override
	protected void initialize() {
    	if (TREE_SIDE > Double.MAX_VALUE) {
    		throw new ArithmeticException("Too big TREE_SIDE: " + TREE_SIDE);
    	}
		insys = world.getSystem(InputSystem.class);
		ausys = world.getSystem(AudioSystem.class);
	}
    
    /**
     * Detects and handles collisions.
     */
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
    	long time = System.nanoTime();
        // TODO space partitioning. quadtree. barnes-hut
    	// http://en.wikipedia.org/wiki/Barnes-Hut_simulation
    	// http://arborjs.org/docs/barnes-hut
    	// http://www.cs.princeton.edu/courses/archive/fall03/cs126/assignments/barnes-hut.html
    	// http://gamedev.stackexchange.com/questions/39931/fast-accurate-2d-collision
    	// http://gamedev.stackexchange.com/questions/41941/faster-2d-collision-detection
    	
        double timeLimit = 1f;
        List<Collision> cs = new ArrayList<Collision>();
        for (int i = 0; i < entities.size(); i++) {
        	Entity e = entities.get(i);
        	VectorD2 p = pm.get(e).vec;
        	double r = rm.get(e).radius;
        	VectorD2 v = pm.get(e).vec;
        	
        	Circle vc = new Circle(p, r+FastMath.sqrt(v.len2()));
        	Circle old = circles.put(e, vc);
        	
        	tree.update(e, old, vc);
        }
//		System.out.println("colUpd: "+(System.nanoTime()-time)*1e-6+" ms");
		tree.getAllCollisions(cs, timeLimit, pm, rm, vm); // TODO slow
//		System.out.println("colInit: "+(System.nanoTime()-time)*1e-6+" ms");
		
        while (!cs.isEmpty()) {
        	long timeH = System.nanoTime();
        	
        	Collections.sort(cs); // reverse sort
        	Collision c = cs.remove(cs.size()-1);
            
            timeLimit -= c.t;
            updatePlanetPositions(entities, c.t); // forward t time
            handleCollision(c.e1, c.e2);
            
            // remove collisions involving c.e1 or c.e2
            for (int i = cs.size() -1; i >= 0; --i) {
            	Collision col = cs.get(i);
            	if (col.e1 == c.e1 || col.e1 == c.e2 || col.e2 == c.e1 || col.e2 == c.e2) {
            		cs.remove(i);
            	} else {
            		col.t -= c.t;
            	}
            }
            VectorD2 p1 = pm.get(c.e1).vec;
            double r1 = rm.get(c.e1).radius;
            VectorD2 v1 = vm.get(c.e1).vec;
			
            VectorD2 p2 = pm.get(c.e2).vec;
            double r2 = rm.get(c.e2).radius;
            VectorD2 v2 = vm.get(c.e2).vec;
            
			Circle vc1 = new Circle(p1, r1+v1.len());
			Circle old1 = circles.put(c.e1, vc1);
			Circle vc2 = new Circle(p2, r2+v2.len());
			Circle old2 = circles.put(c.e2, vc2);
			
            tree.update(c.e1, old1, vc1);
            tree.update(c.e2, old2, vc2);
            
            tree.getCollisions(c.e1, vc1, cs, timeLimit, pm, rm, vm);
            tree.getCollisions(c.e2, vc2, cs, timeLimit, pm, rm, vm);
            timeH = System.nanoTime() - timeH;
//	    	System.out.println("colHandl: "+timeH*1e-6+" ms");
        }
        
        updatePlanetPositions(entities, timeLimit);
        
    	time = System.nanoTime() - time;
//    	System.out.println("colproc: "+time*1e-6+" ms");
//    	System.out.println();
    }
    
    private void updatePlanetPositions(ImmutableBag<Entity> entities, double time) { // O(n)
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            VectorD2 p = pm.get(e).vec;
            VectorD2 v = vm.get(e).vec;
            // p += v*t
            p.add(v.cpy().mul(time));
        }
    }
    
    /** Compare e1 and e2 to all other elements. */
    private void getCollisions(ImmutableBag<Entity> entities, Entity e1, Entity e2, List<Collision> cs, double timeLimit) { // O(n)
    	long time = System.nanoTime();
		VectorD2 p1 = pm.get(e1).vec;
		double r1 = rm.get(e1).radius;
		VectorD2 v1 = vm.get(e1).vec;
		
		VectorD2 p2 = pm.get(e2).vec;
		double r2 = rm.get(e2).radius;
		VectorD2 v2 = vm.get(e2).vec;
		
		for (int i = 0; i < entities.size(); ++i) { // TODO recheck all? int j=0 instead?
			Entity e3 = entities.get(i);
			VectorD2 p3 = pm.get(e3).vec;
			double r3 = rm.get(e3).radius;
			VectorD2 v3 = vm.get(e3).vec;
						
			if (e1 != e3) {
			    double t1 = collisionTime(p1, r1, v1, p3, r3, v3);
			    if (!Double.isNaN(t1) && t1 >= 0 && t1 < timeLimit) {
			        cs.add(new Collision(e1, e3, t1));
			    }
			}
			if (e2 != e3) {
			    double t2 = collisionTime(p2, r2, v2, p3, r3, v3);
			    if (!Double.isNaN(t2) && t2 >= 0 && t2 < timeLimit) {
			        cs.add(new Collision(e2, e3, t2));
			    }
			}
		}
    	time = System.nanoTime() - time;
//    	System.out.println("colGet: "+time*1e-6+" ms");
    }
    
    
    private void getAllCollisions(ImmutableBag<Entity> entities, List<Collision> cs, double timeLimit) { // O(n^2) // TODO EXTREMLY SLOW. NEEDS OPTIMIZATION.
    	long time = System.nanoTime();
		for (int i = 0; i < entities.size(); ++i) {
	    	Entity e1 = entities.get(i);
			VectorD2 p1 = pm.get(e1).vec;
			double r1 = rm.get(e1).radius;
			VectorD2 v1 = vm.get(e1).vec;
			
			for (int j = i+1; j < entities.size(); ++j) {
				Entity e2 = entities.get(j);
				VectorD2 p2 = pm.get(e2).vec;
				double r2 = rm.get(e2).radius;
				VectorD2 v2 = vm.get(e2).vec;
							
			    double t = collisionTime(p1, r1, v1, p2, r2, v2);
			    if (!Double.isNaN(t) && t >= 0 && t < timeLimit) {
			        cs.add(new Collision(e1, e2, t));
			    }
			}
		}
    	time = System.nanoTime() - time;
//    	System.out.println("colAll: "+time*1e-6+" ms");
    }
    
    
    
    public static double collisionTime(VectorD2 p1, double r1, VectorD2 v1, VectorD2 p2, double r2, VectorD2 v2) { // O(1)
    	// TODO: http://twobitcoder.blogspot.se/2010/04/circle-collision-detection.html
		// http://stackoverflow.com/questions/7461081/finding-point-of-collision-moving-circles-time
		// http://en.wikipedia.org/wiki/Elastic_collision
		
		// http://stackoverflow.com/questions/6459035/2d-collision-response-between-circles?rq=1
		// t = (||p|| - ||r1+r2||)/||v||
		// where:
		//   p = p1-p2
		//   v = v1-v2

    	VectorD2 p = p1.cpy().sub(p2);
		VectorD2 v = v1.cpy().sub(v2);
		double r = r1+r2;
		
		// if the planets are already moving away from each other.
	    if (v.dot(p) > 0) {
	        return Double.NaN;
	    }
	    
		// TODO can formula be changed to use len2 instead?
		double pLen = p.len();
	    double vLen = v.len();
	    
	    return (pLen - (r)) / vLen; // TODO if neg.
    }
            
    /**
     * Updates the velocities of two colliding planets.
     */
    private void handleCollision(Entity e1, Entity e2) { // O(1)
    	long time = System.nanoTime();
    	
        VectorD2 p1 = pm.get(e1).vec;
        VectorD2 p2 = pm.get(e2).vec;
        double m1 = mm.get(e1).mass;
        double m2 = mm.get(e2).mass;
        VectorD2 v1 = vm.get(e1).vec;
        VectorD2 v2 = vm.get(e2).vec;
        
		// http://stackoverflow.com/questions/345838/ball-to-ball-collision-detection-and-handling?rq=1
        // http://www.vobarian.com/collisions/2dcollisions2.pdf
        
        VectorD2 p = p1.cpy().sub(p2);
	    
        // normal and tangent
        VectorD2 un = p.cpy().mul(FastMath.inverseSqrt(p.len2())); // normalize
        VectorD2 ut = new VectorD2(-un.y, un.x);
        
        // project on normal and tangent
        double n1 = un.dot(v1);
        double n2 = un.dot(v2);
        double t1 = ut.dot(v1);
        double t2 = ut.dot(v2);
        
        // TODO add other types of collision-handling. non-elastic, melding, breaking, exploding, etc.
        // elastic collision 
        double nn1 = (n1 * (m1-m2) + 2*m2*n2)/(m1+m2);
        double nn2 = (n2 * (m2-m1) + 2*m1*n1)/(m1+m2);
        // t1 and t2 don't change.
        
        // back to vectors.
        VectorD2 nv1 = un.cpy().mul(nn1);
        VectorD2 nv2 = un.cpy().mul(nn2);
        VectorD2 tv1 = ut.cpy().mul(t1);
        VectorD2 tv2 = ut.cpy().mul(t2);
        
        // new velocities
        v1.set(nv1).add(tv1);
        v2.set(nv2).add(tv2);
        
    	time = System.nanoTime()-time;
//    	System.out.println("colHandl: "+time*1e-6+" ms");
    	ausys.playCoin();
    }
    
    
    /**
	 * Checks whether this system is paused.
	 */
	@Override
	protected boolean checkProcessing() {
		return !insys.isPaused() && super.checkProcessing();
	}
	
	@Override
	protected void inserted(Entity e) {
		VectorD2 p = pm.get(e).vec;
		double r = rm.get(e).radius;
		VectorD2 v = vm.get(e).vec;
		Circle vc = new Circle(p, r+v.len());
		tree.add(e, vc);
		circles.put(e,  vc);
	}

	@Override
	protected void removed(Entity e) {
		tree.remove(e);
		circles.remove(e);
	}

}
