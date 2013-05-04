package se.exuvo.planets.systems;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Velocity;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.FastMath;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.Vector2;

/**
 * System responsible for checking for and handling collisions between planets.
 */
public class CollisionSystem extends IntervalEntitySystem {

	@Mapper ComponentMapper<Position> pm;
	@Mapper ComponentMapper<Size> sm;
	@Mapper ComponentMapper<Velocity> vm;
	@Mapper ComponentMapper<Mass> mm;
	@Mapper ComponentMapper<Acceleration> am;
	
	/** Used to check if the game is paused. */
	private InputSystem insys;
	
	
    public CollisionSystem() {
        super(Aspect.getAspectForAll(Position.class, Size.class, Velocity.class, Mass.class), Settings.getFloat("PhysicsStep"));
    }
    
    @Override
	protected void initialize() {
		insys = world.getSystem(InputSystem.class);
	}
    /**
     * Detects and handles collisions.
     */
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        // TODO space partitioning? quadtree?
        
		// TODO IMPORTANT: optimize!
        
        // update accelerations
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            vm.get(e).vec.add(am.get(e).vec);
        }
        
        float timeLimit = 1f;
                                                          
        Collision c;
        
        while ((c = getEarliestCollisions(entities, timeLimit)) != null) {
            updatePlanetPositions(entities, c.t);
            handleCollision(c.e1, c.e2);
            timeLimit -= c.t;
        }
        updatePlanetPositions(entities, timeLimit);
    }
    
    private void updatePlanetPositions(ImmutableBag<Entity> entities, float time) {
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            Vector2 p = pm.get(e).vec;
            Vector2 v = vm.get(e).vec;
            // p += v*t
            p.add(v.cpy().mul(time));
        }
    }
    
    private Collision getEarliestCollisions(ImmutableBag<Entity> entities, float timeLimit) {
    	// TODO selectively check planets. quadtree.
    	// TODO reuse found collision (not just the most immediate
        Collision c = null;
        for (int i = 0; i < entities.size(); ++i) {
			Entity e1 = entities.get(i);
			Vector2 p1 = pm.get(e1).vec;
			Size s1 = sm.get(e1);
			Vector2 v1 = vm.get(e1).vec;
			
			for (int j = i+1; j < entities.size(); ++j) { // TODO recheck all? int j=0 instead?
    			Entity e2 = entities.get(j);
    			Vector2 p2 = pm.get(e2).vec;
    			Size s2 = sm.get(e2);
    			Vector2 v2 = vm.get(e2).vec;
    			
    			// TODO: http://twobitcoder.blogspot.se/2010/04/circle-collision-detection.html
    			// http://stackoverflow.com/questions/7461081/finding-point-of-collision-moving-circles-time
    			// http://en.wikipedia.org/wiki/Elastic_collision
    			
    			// according to:
    			// http://stackoverflow.com/questions/6459035/2d-collision-response-between-circles?rq=1
    			// we have that:
    			// t = (||p|| - ||r1+r2||)/||v||
    			// where:
    			//   p = p1-p2
    			//   v = v1-v2
    			
    			// TODO check if p.len2()>large to speed up?
    			// TODO time offsets
    			Vector2 p = p1.cpy().sub(p2);
    			Vector2 v = v1.cpy().sub(v2);
    			
    			// if planets are already moving away from each other.
			    if (v.dot(p) > 0) {
			        continue;
			    }
			    
    			// TODO can formula be changed to use len2 instead?
    			float pLen = p.len();
			    float vLen = v.len();
    			float r1 = s1.radius;
			    float r2 = s2.radius;
//			    System.out.println("v1:"+v1.vec.len()+" "+v1.vec);
//			    System.out.println("v2:"+v2.vec.len()+" "+v2.vec);
//			    System.out.println("p:"+pLen+" "+p);
//			    System.out.println("v:"+vLen+" "+v);
			    
			    float t = (pLen - (r1+r2)) / vLen;
//			    System.out.println("tn:"+t);
			    
			    // TODO check for vLen==0 instead?
			    // TODO does t have to be >= 0
			    if (!Float.isNaN(t) && t >= 0 && t < timeLimit && (c == null || t < c.t)) {
//    			    System.out.println("t:"+t);
			        c = new Collision(e1, e2, t);
			    }
			}
        }
        return c;
    }
    
    /**
     * Updates the velocities of two colliding planets.
     */
    private void handleCollision(Entity e1, Entity e2) {
        Vector2 p1 = pm.get(e1).vec;
        Vector2 p2 = pm.get(e2).vec;
        float m1 = mm.get(e1).mass;
        float m2 = mm.get(e2).mass;
        Vector2 v1 = vm.get(e1).vec;
        Vector2 v2 = vm.get(e2).vec;
        float r1 = sm.get(e1).radius;
        float r2 = sm.get(e2).radius;
        
        
//        System.out.println("p1:"+p1.len()+" "+p1);
//        System.out.println("p2:"+p2.len()+" "+p2);
		// http://stackoverflow.com/questions/345838/ball-to-ball-collision-detection-and-handling?rq=1
        // http://www.vobarian.com/collisions/2dcollisions2.pdf
        
        // TODO optimize.
        // elastic collision: // TODO add other types of collision-handling. melding, breaking, exploding, etc.
        
        Vector2 p = p1.cpy().sub(p2);
//        System.out.println("p:"+p.len()+" "+p2);
//        System.out.println("r:"+(r1+r2));
        
        
        // normal and tangent
//        Vector2 un = p.cpy().nor();
        Vector2 un = p.cpy().mul((float) FastMath.inverseSqrt(p.len2())); // normalize
        Vector2 ut = new Vector2(-un.y, un.x);
        
//        System.out.println("un:"+un);
//        System.out.println("ut:"+ut);
        
        // projection magnitudes
        float n1 = un.dot(v1);
        float n2 = un.dot(v2);
        float t1 = ut.dot(v1);
        float t2 = ut.dot(v2);
        
        // TODO use a percentage to generate heat?
        float nn1 = (n1 * (m1-m2) + 2*m2*n2)/(m1+m2);
        float nn2 = (n2 * (m2-m1) + 2*m1*n1)/(m1+m2);
        // t1 and t2 don't change.
        
        // back to vectors.
        Vector2 nv1 = un.cpy().mul(nn1);
        Vector2 nv2 = un.cpy().mul(nn2);
        Vector2 tv1 = ut.cpy().mul(t1);
        Vector2 tv2 = ut.cpy().mul(t2);
        
//        System.out.println(e1+" "+v1);
//        System.out.println(e2+" "+v2);
        // new velocities
        v1.set(nv1).add(tv1);
        v2.set(nv2).add(tv2);
//        System.out.println(e1+" "+v1);
//        System.out.println(e2+" "+v2);
    }
    
    
    /**
     * Holds the data of an detected Collision:
     * the indices of the involved planets and the time (0 <= t < 1) that it happens.
     */
    private class Collision {
        public final float t;
        public final Entity e1, e2; // planets
        
        public Collision(Entity e1, Entity e2, float t) {
            this.e1 = e1;
            this.e2 = e2;
            this.t = t;
        }
    }
    
    
    /**
	 * Checks whether this system is paused.
	 */
	@Override
	protected boolean checkProcessing() {
		return !insys.isPaused() && super.checkProcessing();
	}
}
