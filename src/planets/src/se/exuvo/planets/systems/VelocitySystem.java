package se.exuvo.planets.systems;

import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Velocity;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.badlogic.gdx.Gdx;

/**
 * System responsible for "moving" planets (usually)
 * - updating their positions using their velocities.
 */
public class VelocitySystem extends IntervalEntityProcessingSystem {
	
	// --variables--
	/** Mapper for entities with the Velocity-Aspect. */
	@Mapper	ComponentMapper<Velocity> vm;
	/** Mapper for entities with the Position-Aspect. */
	@Mapper	ComponentMapper<Position> pm;

	private float maxX, maxY, minX, minY; // TODO use some sort of rectangle-object instead?
	
	/** Whether this system is paused. */
	private InputSystem insys;
	
	// --constructor--
	/**
	 * Creates a new VelocitySystem.
	 */
	public VelocitySystem() {
		super(Aspect.getAspectForAll(Velocity.class, Position.class), Settings.getFloat("PhysicsStep"));
		maxX = Gdx.graphics.getWidth()/2;
		maxY = Gdx.graphics.getHeight()/2;
		minX = -maxX;
		minY = -maxY;
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
		
		// apply velocity to position
		p.vec.add(v.vec); // p+=v
		
		// TODO easier to create systems without.
//		if(p.vec.x < minX) p.vec.x = minX;
//		if(p.vec.y < minY) p.vec.y = minY;
//		if(p.vec.x > maxX) p.vec.x = maxX;
//		if(p.vec.y > maxY) p.vec.y = maxY;
		
		//DEBUG:
//		System.out.println(e+"v:"+v.vec.len2()+": "+v.vec.x +" "+ v.vec.y);
	}
	
	/**
	 * Checks whether this system is set to pause.
	 */
	@Override
	protected boolean checkProcessing() {
		// paused super res
		// 1		1    0
		// 1		0    0
		// 0		1    1
		// 0		0    0
		//return !paused && super.checkProcessing();
		
		if (insys.isPaused()) {
			return false;
		}
		return super.checkProcessing();
	}
	
}
