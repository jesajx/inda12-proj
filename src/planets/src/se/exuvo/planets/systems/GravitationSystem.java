package se.exuvo.planets.systems;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.Utils;
import com.badlogic.gdx.math.Vector2;

public class GravitationSystem extends IntervalEntitySystem {
	@Mapper ComponentMapper<Mass> mm;
	@Mapper ComponentMapper<Acceleration> am;
	@Mapper ComponentMapper<Position> pm;
	
	private float G = (float) (6.6726 * Math.pow(10.0,-11.0));

	public GravitationSystem() {
		super(Aspect.getAspectForAll(Mass.class, Acceleration.class, Position.class), Settings.getFloat("PhysicsStep"));
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			Mass m = mm.get(e);
			Acceleration a = am.get(e);
			Position p = pm.get(e);
			
			// clear old accel
			a.vec.set(Vector2.Zero);
			
			// calculate gravity effects
			for (int j = 0; j < entities.size(); j++) {
				Entity e2 = entities.get(j);
				if(e2 == e) continue;
				
				Mass m2 = mm.get(e2);
				Position p2 = pm.get(e2);
				
				Vector2 positionDiff = p2.vec.cpy().sub(p.vec);
				
				float distance = positionDiff.len2();
				Vector2 v = new Vector2(1 * (m.mass * m2.mass) / distance, 0);
				System.out.println(positionDiff.angle());
				a.vec.add(v.rotate(positionDiff.angle()));
				
			}
		}

		// use FastMath TrigLUT Utils from artemis or MathUtils from libgdx?

	}

}
