package se.exuvo.planets.systems;

import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Velocity;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;

/**
 * System responsible for "moving" planets (usually) - updating their positions using their velocities.
 */
public class VelocitySystem extends IntervalEntityProcessingSystem {

	/** Mapper for entities with the Velocity-Aspect. */
	@Mapper ComponentMapper<Velocity> vm;
	/** Mapper for entities with the Position-Aspect. */
	@Mapper ComponentMapper<Position> pm;

	/** Whether this system is paused. */
	private InputSystem insys;

	public VelocitySystem() {
		super(Aspect.getAspectForAll(Velocity.class, Position.class), Settings.getFloat("PhysicsStep"));
	}

	@Override
	protected void initialize() {
		insys = world.getSystem(InputSystem.class);
	}

	/**
	 * Updates the position of the given entity using its velocity.
	 */
	@Override
	protected void process(Entity e) {
		Position p = pm.get(e);
		Velocity v = vm.get(e);

		p.vec.add(v.vec);
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