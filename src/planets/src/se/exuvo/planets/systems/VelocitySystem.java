package se.exuvo.planets.systems;

import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Velocity;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;

public class VelocitySystem extends IntervalEntityProcessingSystem {
	@Mapper	ComponentMapper<Velocity> vm;
	@Mapper	ComponentMapper<Position> pm;

	public VelocitySystem() {
		super(Aspect.getAspectForAll(Velocity.class, Position.class), Settings.getFloat("PhysicsStep"));
	}

	@Override
	protected void process(Entity e) {
		Position p = pm.get(e);
		Velocity v = vm.get(e);
		
		// apply speed to position
		p.vec.add(v.vec);
	}

}
