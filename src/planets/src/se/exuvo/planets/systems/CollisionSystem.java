package se.exuvo.planets.systems;

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
	
	/**
	 * Creates a new CollisionSystem.
	 */
    public CollisionSystem() {
        super(Aspect.getAspectForAll(Position.class, Size.class, Velocity.class), Settings.getFloat("PhysicsStep"));
    }

    /**
     * Detects and handles collisions.
     */
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        
        // TODO map colliding times and planets
        
        for (int i = 0; i < entities.size(); i++) {
			
			Entity e1 = entities.get(i);
			Position p1 = pm.get(e1);
			Size s1 = sm.get(e1);
			Velocity v1 = vm.get(e1);
			Mass m1 = mm.get(e1);
			
		    float min_t = Float.POSITIVE_INFINITY;
		    Entity min_e = null;
			
			for (int j = i+1; j < entities.size(); j++) {
    			Entity e2 = entities.get(i);
    			Position p2 = pm.get(e2);
    			Size s2 = sm.get(e2);
    			Velocity v2 = vm.get(e2);
    			Mass m2 = mm.get(e2);
    			
    			// http://stackoverflow.com/questions/7461081/finding-point-of-collision-moving-circles-time
    			// http://stackoverflow.com/questions/345838/ball-to-ball-collision-detection-and-handling?rq=1
    			// http://en.wikipedia.org/wiki/Elastic_collision
    			
    			// according to:
    			// http://stackoverflow.com/questions/6459035/2d-collision-response-between-circles?rq=1
    			// we have that:
    			// t = (||p|| - ||r1+r2||)/||v||
    			// where:
    			//   p = p1-p2
    			//   v = v1-v2
    			
    			// TODO optimize!
    			// TODO clean!
    			
    			Vector2 p = p1.vec.sub(p2.vec);
    			Vector2 v = v1.vec.sub(v2.vec);
    			float pLen = p.len();
			    float vLen = v.len();
    			float r1 = s1.radius;
			    float r2 = s2.radius;
			    
    			// TODO can formula be changed to use len2 instead?
			    float t = (pLen - (r1+r2)) / vLen;
			    
			    // TODO check for vLen==0 instead?
			    if (!Float.isNaN(t) && t < min_t) {
			        min_t = t; // TODO
			    }
			}
        }
        
        // TODO handle collisions
    }
    
    private class Collision {
        float time;
        Entity p1, p2;
        public Collision(Entity p1, Entity p2, float time) {
            this.p1 = p1;
            this.p2 = p2;
            this.time = time;
        }
    }
}
