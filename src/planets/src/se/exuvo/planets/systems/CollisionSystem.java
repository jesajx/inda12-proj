package se.exuvo.planets.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.Vector2;

/**
 * System responsible for checking for and handling collisions between planets.
 */
public class CollisionSystem extends IntervalEntitySystem {

    /** Mapper for entities with the Position-component. */
	@Mapper ComponentMapper<Position> pm;
    /** Mapper for entities with the Size-component. */
	@Mapper ComponentMapper<Size> sm;
    /** Mapper for entities with the Velocity-component. */
	@Mapper ComponentMapper<Velocity> vm;
    /** Mapper for entities with the Mass-component. */
	@Mapper ComponentMapper<Mass> mm;
	
	// TODO involve Acceleration?
	
	
    public CollisionSystem() {
        super(Aspect.getAspectForAll(Position.class, Size.class, Velocity.class, Mass.class), Settings.getFloat("PhysicsStep"));
    }

    /**
     * Detects and handles collisions.
     */
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        
        // TODO space partitioning? quadtree?
        
		// TODO optimize!
		// TODO clean!
        
        List<Collision> collisions = new ArrayList<Collision>(entities.size()); // TODO "SortedList" instead?
        
        // times>0 if planets are moved due to collisions.
        float[] timeOffsets = new float[entities.size()]; // init. 0.0
                                                          
        
        getCollisions(entities, collisions, timeOffsets);
        while (!collisions.isEmpty()) {
            // TODO store collisions differently somehow so we don't have to sort?
            Collections.sort(collisions); // TODO inverse-sort instead?
            
            // handle the earliest collision.
            Collision c = collisions.remove(0);
            
            handleCollision(c, entities, timeOffsets);
            
            timeOffsets[c.i] = c.t; 
            timeOffsets[c.j] = c.t;
            
            collisions.clear(); // TODO reuse found collisions to simplify getCollisions() accordingly
            getCollisions(entities, collisions, timeOffsets);
        }
    }
    
    private void getCollisions(ImmutableBag<Entity> entities, List<Collision> collisions, float[] timeOffsets) {
        for (int i = 0; i < entities.size(); ++i) {
			Entity e1 = entities.get(i);
			Position p1 = pm.get(e1);
			Size s1 = sm.get(e1);
			Velocity v1 = vm.get(e1);
			Mass m1 = mm.get(e1);
			
			for (int j = i+1; j < entities.size(); ++j) { // TODO recheck all? int j=0 instead?
    			Entity e2 = entities.get(j);
    			Position p2 = pm.get(e2);
    			Size s2 = sm.get(e2);
    			Velocity v2 = vm.get(e2);
    			Mass m2 = mm.get(e2);
    			
    			// http://stackoverflow.com/questions/7461081/finding-point-of-collision-moving-circles-time
    			// http://en.wikipedia.org/wiki/Elastic_collision
    			
    			// according to:
    			// http://stackoverflow.com/questions/6459035/2d-collision-response-between-circles?rq=1
    			// we have that:
    			// t = (||p|| - ||r1+r2||)/||v||
    			// where:
    			//   p = p1-p2
    			//   v = v1-v2
    			
    			Vector2 po1 = p1.vec.cpy(), po2 = p2.vec.cpy();
    			float offset;
    			if (timeOffsets[i] < timeOffsets[j]) {
    			    offset = timeOffsets[i];
    			    po2.mul(offset);
    			} else {
    			    offset = timeOffsets[j];
    			    po1.mul(offset);
    			}
    			
    			
    			// TODO check if p.len2()>large to speed up?
    			// TODO time offsets
    			Vector2 p = po1.cpy().sub(po2);
    			Vector2 v = v1.vec.cpy().sub(v2.vec);
    			float pLen = p.len();
			    float vLen = v.len();
    			float r1 = s1.radius;
			    float r2 = s2.radius;
//			    System.out.println("v1:"+v1.vec.len()+" "+v1.vec);
//			    System.out.println("v2:"+v2.vec.len()+" "+v2.vec);
//			    System.out.println("p:"+pLen+" "+p);
//			    System.out.println("v:"+vLen+" "+v);
			    
    			// TODO can formula be changed to use len2 instead?
			    float t = (pLen - (r1+r2)) / vLen + offset;
			    
			    // TODO check for vLen==0 instead?
			    // t=1 is the next update, so we're not interested in times beyond that.
			    if (!Float.isNaN(t) && t >= 0 && t < 1) {
			        collisions.add(new Collision(i, j, t));
			    }
			}
        }
    }
    
    private void handleCollision(Collision c, ImmutableBag<Entity> entities, float[] timeOffsets) {
        Entity e1 = entities.get(c.i);
        Entity e2 = entities.get(c.j);
        Vector2 p1 = pm.get(e1).vec;
        Vector2 p2 = pm.get(e2).vec;
        float m1 = mm.get(e1).mass;
        float m2 = mm.get(e2).mass;
        Vector2 v1 = pm.get(e1).vec;
        Vector2 v2 = pm.get(e2).vec;
        float r1 = sm.get(e1).radius;
        float r2 = sm.get(e2).radius;
        
		// http://stackoverflow.com/questions/345838/ball-to-ball-collision-detection-and-handling?rq=1
        // http://www.vobarian.com/collisions/2dcollisions2.pdf
        
        // TODO optimize.
        // elastic collision:
        
        // move planets to the collision
        p1.add(v1.cpy().mul(c.t - timeOffsets[c.i]));
        p2.add(v2.cpy().mul(c.t- timeOffsets[c.j]));
        
        // normal and tangent
        Vector2 un = p1.cpy().sub(p2).nor();
        Vector2 ut = new Vector2(-un.y, un.x); // TODO method to do this?
        
        System.out.println("un:"+un);
        System.out.println("ut:"+ut);
        // projection magnitudes
        float n1 = un.dot(v1);
        float n2 = un.dot(v2);
        float t1 = ut.dot(v1);
        float t2 = ut.dot(v2);
        
        float nn1 = (n1 * (m1-m2) + 2*m2*n2)/(m1+m2);
        float nn2 = (n2 * (m2-m1) + 2*m1*n1)/(m1+m2);
        // t1 and t2 don't change.
        
        // back to vectors.
        Vector2 nv1 = un.cpy().mul(nn1);
        Vector2 nv2 = un.cpy().mul(nn2);
        Vector2 tv1 = ut.cpy().mul(t1);
        Vector2 tv2 = ut.cpy().mul(t2);
        
        // new velocities
        v1.set(nv1).add(tv1);
        v1.set(nv2).add(tv2);
        System.out.println(e1+" "+v1);
        System.out.println(e2+" "+v2);
    }
    
    
    
    /**
     * Holds the data of an detected Collision:
     * the indices of the involved planets and the time (0 <= t < 1) that it happens.
     */
    private class Collision implements Comparable<Collision> {
        public final float t;
        public final int i, j; // planets
        
        public Collision(int i, int j, float t) {
            this.i = i;
            this.j = j;
            this.t = t;
        }

        @Override
        public int compareTo(Collision o) {
            return Float.compare(t, o.t);
        }
    }
}
