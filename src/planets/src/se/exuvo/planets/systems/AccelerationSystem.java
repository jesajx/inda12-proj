package se.exuvo.planets.systems;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Velocity;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;

public class AccelerationSystem extends IntervalEntityProcessingSystem {
	@Mapper	ComponentMapper<Velocity> vm;
	@Mapper	ComponentMapper<Acceleration> am;

	public AccelerationSystem() {
		super(Aspect.getAspectForAll(Velocity.class, Acceleration.class), Settings.getFloat("PhysicsStep"));
	}

	@Override
	protected void process(Entity e) {
		Velocity v = vm.get(e);
		Acceleration a = am.get(e);
		
		// apply accel to speed
		v.vec.add(a.vec);
		
		//DEBUG:
//		System.out.println(e+"a:"+a.vec.x +" "+ a.vec.y);
	}

}
