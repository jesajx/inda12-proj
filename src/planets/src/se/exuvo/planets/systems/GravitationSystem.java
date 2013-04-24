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
import com.artemis.utils.FastMath;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class GravitationSystem extends IntervalEntitySystem {
	
	//--variables--
	// TODO move to separate Constants-class?
	/**
	 * The Gravitational Constant.
	 * TODO Should have the same value as in reality if we want to use realistic masses/distances/times/etc. for planets.
	 * 		This means planets need to have gigantic masses, distances and time to properly orbit. We may need to change this.
	 */
	private float G = 6.6726e-11f;
	
	/**
	 * The kg-mass of Terra - Earth.
	 * Useful reference when setting other planets' masses.
	 */
	private float earth_mass = 5.9376e24f;
	
	/**
	 * The kg-mass of Sol, the realworld sun.
	 * Useful reference when setting other stars' masses.
	 */
	private float sol_mass = 1.9891e30f;
	
	
	/**
	 * Gives the system access to components with the Mass-Aspect.
	 */
	@Mapper ComponentMapper<Mass> mm;
	
	/**
	 * Gives the system access to components with the Acceleration-Aspect.
	 */
	@Mapper ComponentMapper<Acceleration> am;
	
	/**
	 * Gives the system access to components with the Position-Aspect.
	 */
	@Mapper ComponentMapper<Position> pm;
	
	

	/**
	 * Creates a new GravitationSystem.
	 */
	public GravitationSystem() {
		super(Aspect.getAspectForAll(Mass.class, Acceleration.class, Position.class), Settings.getFloat("PhysicsStep"));
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		
		// clear old accelerations
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			Acceleration a = am.get(e);
			a.vec.set(Vector2.Zero); // reset
		}
		
		// calculate gravity effects on gravity.
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			Acceleration a = am.get(e);
			
			Mass m = mm.get(e);
			Position p = pm.get(e);
			
			for (int j = i+1; j < entities.size(); j++) {
				Entity e2 = entities.get(j);
				Acceleration a2 = am.get(e);
				
				Mass m2 = mm.get(e2);
				Position p2 = pm.get(e2);
				
				// F = G*m*M/d^2
				// F = ma
				// a = G*M/d^2 = M * G/d^2
				// where:
				// m = e.mass
				// M = e2.mass
				// G = grav. constant
				// d = distance between e and e2
				
				
				
				// vector from p to p2
				Vector2 posDiff = p2.vec.cpy().sub(p.vec);
				
				// distance from p to p2
				float distanceSquared = posDiff.len2(); // distance squared
				// angle from p to p2
				float angle = MathUtils.atan2(posDiff.y, posDiff.x); // should be fast.
				if (angle < 0) {
					angle = (float) FastMath.HALF_PI;
				}
				angle %= FastMath.PI;
				
				// G/d^2
				float k = G / distanceSquared;
				
				if (!Double.isNaN(k)) { // TODO necessary?
				
					// the accelerations are created as:
					//
					// <-----.-----> (0 angle)
					//
					// and then rotated counter-clockwise.
					
					// acceleration for p towards p2
					Vector2 v = pol(angle, m2.mass * k); // vector from angle and magnitude
					a.vec.add(v);
					
					// acceleration for p2 towards p
					Vector2 v2 = pol(angle, -m.mass * k);
					a2.vec.add(v2);
					
					//DEBUGGING.
					System.out.println("m2*k:" + m2.mass * k);
					System.out.println("m*k:" + m.mass * k);
					System.out.println("d^2: " + distanceSquared); 
					System.out.println("G: " + G); 
					System.out.println("k: " + k); 
					System.out.println("rads: " + angle); 
					System.out.println("v: " + v.x +" "+ v.y);
					System.out.println("v2: " + v2.x +" "+ v2.y);
					System.out.println("a: " + a.vec.x +" "+ a.vec.y);
					System.out.println("a2: " + a2.vec.x +" "+ a2.vec.y);
				}
			}
		}
		
		// TODO use FastMath TrigLUT Utils from artemis or MathUtils from libgdx?
		// NOTE: I've used libgdx's MathUtils for fast sin,cos and atan2. Vector2 doesn't have that good support of pol and angles on it's own.
		// NOTE: libgdx's MathUtils uses lookuptables created using java.lang.Math -> java.lang.StrictMath -> C-code.
		// NOTE: artemis's FastMath uses some other lookup-method. Probably faster, but less accurate, than gdx's.
		// NOTE: artemis's FastMath had more accurate PI-variables.
	}
	
	/**
	 * Creates a {@link Vector2} from the given polar arguments.
	 */
	private static Vector2 pol(float angle, float magnitude) {
		float cos = (float)MathUtils.cos(angle);
		float sin = (float)MathUtils.sin(angle);
		return new Vector2(magnitude*cos, magnitude*sin);
	}
}
