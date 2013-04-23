package se.exuvo.planets.systems;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Mass;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;

public class GravitationSystem extends IntervalEntitySystem {
	@Mapper ComponentMapper<Mass> mm;
	@Mapper ComponentMapper<Acceleration> am;

	public GravitationSystem() {
		super(Aspect.getAspectForAll(Mass.class, Acceleration.class), Settings.getFloat("PhysicsStep"));
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		// TODO Auto-generated method stub
		
		// clear old accel
		
		// calculate gravity effects
		
		// use FastMath TrigLUT Utils from artemis ?

	}

}
