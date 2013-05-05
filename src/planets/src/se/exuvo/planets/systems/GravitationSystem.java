package se.exuvo.planets.systems;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.utils.QuadTree;
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

/**
 * System responsible for updating the acceleration of planets,
 * by calculating gravity between planets.
 */
public class GravitationSystem extends IntervalEntitySystem {
	
	// TODO move to separate Constants-class?
	/**
	 * The Gravitational Constant.
	 * TODO Should have the same value as in reality if we want to use realistic masses/distances/times/etc. for planets. This does however mean that planets need to have GIGANTIC masses, distances and time to properly orbit.
	 */
	private float G = 6.6726e-11f;
	public float side = 1e6f; // TODO globalize
	public QuadTree tree = new QuadTree(new Vector2(-side/2, -side/2), side);// TODO increase size of the universe.
	
	/** Gives the system access to components with the Mass-Aspect. */
	@Mapper ComponentMapper<Mass> mm;
	
	/** Gives the system access to components with the Acceleration-Aspect. */
	@Mapper ComponentMapper<Acceleration> am;
	
	/** Gives the system access to components with the Position-Aspect. */
	@Mapper ComponentMapper<Position> pm;
	
	

	public GravitationSystem() {
		super(Aspect.getAspectForAll(Mass.class, Acceleration.class, Position.class), Settings.getFloat("PhysicsStep"));
	}

	/**
	 * Updates the acceleration of the given entities by calculating gravitational effects.
	 * The acceleration of the planets are reset to (0,0).
	 * Then each planet is compared to all other planets once, and the gravitational acceleration
	 * between each pair is calculated and added to respective planet.
	 * The method has the time complexity of this method is O(n<sup>2</sup>) and may change in the future.
	 */
	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		long time  = System.nanoTime();
        // TODO space partitioning. quadtree. barnes-hut
    	// http://en.wikipedia.org/wiki/Barnes-Hut_simulation
    	// http://arborjs.org/docs/barnes-hut
		// http://www.cs.princeton.edu/courses/archive/fall03/cs126/assignments/barnes-hut.html
		
		tree.update(mm, pm);
		
		for (int i = 0; i < entities.size(); i++) { // update accelerations
            Entity e = entities.get(i);
            Vector2 a = am.get(e).vec;
            a.set(0f,0f);
			tree.updateAcceleration(entities.get(i), 0.5f, 6.6726e-11f, mm, pm, am);
		}

		// update accelerations
		for (int i = 0; i < entities.size(); i++) { // TODO update Acceleration in separate system? works if the accelerations are processed before the gravitation.
			Entity e = entities.get(i);
			Acceleration a = am.get(e);
			a.vec.set(Vector2.Zero); // reset
		}
		
		// calculate gravity effects on each pair of planets.
		for (int i = 0; i < entities.size(); i++) {
			
			// planet A
			Entity e = entities.get(i);
			Acceleration a = am.get(e);
			Mass m = mm.get(e);
			Position p = pm.get(e); //TODO center of planet A? or top left corner?
			
			for (int j = i+1; j < entities.size(); j++) {
				
				// planet B
				Entity e2 = entities.get(j);
				Acceleration a2 = am.get(e2);
				Mass m2 = mm.get(e2);
				Position p2 = pm.get(e2);//TODO center of planet B? or top left corner?
				

				// F = G*m*M/d^2
				// F = ma
				// a = G*M/d^2
				// a = M * G/d^2
				// k = G/d^2
				// a = M*k
				// where:
				// m = e.mass
				// M = e2.mass
				// G = grav. constant
				// d = distance from e and e2

				
                Vector2 diff = p2.vec.cpy().sub(p.vec);
                float d2 = diff.len2();
                float k = G / d2;
                if (Float.isNaN(k)) { // if distanceSquared==0
                    continue;
                }
                diff.mul((float) (k*FastMath.inverseSqrt(d2))); // normalize and mul(k)
                Vector2 v = diff.cpy().mul(m2.mass);
                Vector2 v2 = diff/*.cpy()*/.mul(-m.mass);
				a.vec.add(v);
				a2.vec.add(v2);

                
                				

				// TODO remove old algorithm (commented below)
//				// vector from p to p2
//				Vector2 posDiff = p2.vec.cpy().sub(p.vec);
//				
//				// distance from p to p2
//				float distanceSquared = posDiff.len2(); // distance squared
//				
//				// angle (in radians) from p to p2
//				float angle = MathUtils.atan2(posDiff.y, posDiff.x); // should be fast.
//				
//				// G/d^2
//				float k = G / distanceSquared;
//				
//				// incase the planets are overlapping exactly.
//				if (distanceSquared == 0) {
//						continue;
//				}
//
//
//				// magnitudes of accelerations between planets A and B
//				float mag1 = m2.mass*k;
//				float mag2 = -m.mass*k; // negative because it goes in the opposite direction.
//				float cos = (float)MathUtils.cos(angle);
//				float sin = (float)MathUtils.sin(angle);
//				
//				// create and set acceleration for p towards p2
//				Vector2 v = new Vector2(mag1*cos, mag1*sin);
//				a.vec.add(v);
//				
//				// create and set acceleration for p2 towards p
//				Vector2 v2 = new Vector2(mag2*cos, mag2*sin);
//				a2.vec.add(v2);
				
				//DEBUGGING.
//				System.out.println("m2*k:" + m2.mass * k);
//				System.out.println("m*k:" + m.mass * k);
//				System.out.println("d^2: " + distanceSquared); 
//				System.out.println("G: " + G); 
//				System.out.println("k: " + k); 
//				System.out.println("rads: " + angle); 
//				System.out.println("v: " + v.x +" "+ v.y);
//				System.out.println("v2: " + v2.x +" "+ v2.y);
//				System.out.println("a: " + a.vec.x +" "+ a.vec.y);
//				System.out.println("a2: " + a2.vec.x +" "+ a2.vec.y);
			}
		}
		
		// TODO use FastMath TrigLUT Utils from artemis or MathUtils from libgdx?
		// NOTE: I've used libgdx's MathUtils for fast sin,cos and atan2.
		// NOTE: libgdx's MathUtils uses lookuptables created using java.lang.Math -> java.lang.StrictMath -> C-code.
		// NOTE: artemis's FastMath uses some other lookup-method. Possibly faster but less accurate than gdx's.
		// NOTE: artemis's FastMath had more accurate PI-variables.
		time  = System.nanoTime() -time;
		System.out.println("grav: "+time*1e-6+" ms");
	}
	
}
