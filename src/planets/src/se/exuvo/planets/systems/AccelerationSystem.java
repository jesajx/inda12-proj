package se.exuvo.planets.systems;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Velocity;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;

/**
 * System responsible for updating the {@link Velocity} of planets (usually),
 * using their {@link Acceleration}.
 */
public class AccelerationSystem extends IntervalEntityProcessingSystem {
	/** Mapper for Entities with the Velocity-aspect. */
	@Mapper	ComponentMapper<Velocity> vm;
	/** Mapper for Entities with the Acceleration-aspect. */
	@Mapper	ComponentMapper<Acceleration> am;

	/**
	 * Creates a new AccelerationSystem.
	 */
	public AccelerationSystem() {
		super(Aspect.getAspectForAll(Velocity.class, Acceleration.class), Settings.getFloat("PhysicsStep"));
	}

	/**
	 * Updates the velocity of the given Entity, using its acceleration.
	 */
	@Override
	protected void process(Entity e) {
		Velocity v = vm.get(e);
		Acceleration a = am.get(e);
		
		// apply accel to speed
		v.vec.add(a.vec); // v+=a
		
		//DEBUG:
//		System.out.println(e+"a:"+a.vec.x +" "+ a.vec.y);
	}

}
