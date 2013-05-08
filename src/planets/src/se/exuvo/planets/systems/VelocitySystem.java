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
 * System responsible for updating the {@link Velocity} of planets (usually), using their {@link Acceleration}.
 */
public class VelocitySystem extends IntervalEntityProcessingSystem { // TODO rename to VelocitySystem? since it is the velocities it
// changes.
	/** Mapper for Entities with the Velocity-aspect. */
	@Mapper ComponentMapper<Velocity> vm;
	/** Mapper for Entities with the Acceleration-aspect. */
	@Mapper ComponentMapper<Acceleration> am;

	private InputSystem insys;

	/**
	 * Creates a new AccelerationSystem.
	 */
	public VelocitySystem() {
		super(Aspect.getAspectForAll(Velocity.class, Acceleration.class), Settings.getFloat("PhysicsStep"));
	}

	@Override
	protected void initialize() {
		insys = world.getSystem(InputSystem.class);
	}

	/**
	 * Updates the velocity of the given Entity, using its acceleration.
	 */
	@Override
	protected void process(Entity e) {
		Velocity v = vm.get(e);
		Acceleration a = am.get(e);

		// apply acceleration to velocity
		v.vec.add(a.vec); // v+=a
	}

	/**
	 * Checks whether this system is set to pause.
	 */
	@Override
	protected boolean checkProcessing() {
		if (insys.isPaused()) {
			return false;
		}
		return super.checkProcessing();
	}
}
