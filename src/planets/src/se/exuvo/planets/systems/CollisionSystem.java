package se.exuvo.planets.systems;

import se.exuvo.planets.components.Acceleration;
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
	
	/**
	 * Creates a new CollisionSystem.
	 */
    public CollisionSystem() {
        super(Aspect.getAspectForAll(Position.class, Size.class, Velocity.class), Settings.getFloat("PhysicsStep"));
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {
			
			// planet A
			Entity e = entities.get(i);
			Position p = pm.get(e);
			Size s = sm.get(e);
			Velocity v = vm.get(e);
			
			for (int j = i+1; j < entities.size(); j++) {
    			Entity e2 = entities.get(i);
    			Position p2 = pm.get(e);
    			Size s2 = sm.get(e);
    			Velocity v2 = vm.get(e);
    			
    			// P = p + v*t
    			// P2 = p2 + v2*t
    			// collision if centers at time t if P=P2
    			// p +v*t = p2 + v2*t
    			// p-p2 = (v-v2)*t
    			// t = (p-p2)/(v-v2)
    			
    			if (v.vec.sub(v2.vec).len2() != 0) {
    			    
    			}
			}
        }
    }

}
