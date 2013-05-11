package se.exuvo.planets.systems;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.utils.GravQuadTree;
import se.exuvo.planets.utils.VectorD2;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;

/**
 * System responsible for updating the acceleration of planets, by calculating gravity between planets.
 */
public class GravitationSystem extends EntitySystem {

	// TODO move to separate Constants-class?
	/**
	 * The Gravitational Constant. TODO Should have the same value as in reality if we want to use realistic masses/distances/times/etc. for
	 * planets. This does however mean that planets need to have GIGANTIC masses, distances and time to properly orbit.
	 */
	private float G = 6.6726e-11f;
	private float theta = 0.7f;
	public float side = 1e20f; // TODO globalize
	public GravQuadTree tree = new GravQuadTree(new VectorD2(-side / 2, -side / 2), side);// TODO increase size of the universe.

	/** Gives the system access to components with the Mass-Aspect. */
	@Mapper ComponentMapper<Mass> mm;

	/** Gives the system access to components with the Acceleration-Aspect. */
	@Mapper ComponentMapper<Acceleration> am;

	/** Gives the system access to components with the Position-Aspect. */
	@Mapper ComponentMapper<Position> pm;

	public GravitationSystem() {
		super(Aspect.getAspectForAll(Mass.class, Acceleration.class, Position.class));
	}

	@Override
	protected void initialize() {}

	/**
	 * Updates the acceleration of the given entities by calculating gravitational effects. The acceleration of the planets are reset to
	 * (0,0). Then each planet is compared to all other planets once, and the gravitational acceleration between each pair is calculated and
	 * added to respective planet. The method has the time complexity of this method is O(n<sup>2</sup>) and may change in the future.
	 */
	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		long time = System.nanoTime();

		tree.update(mm, pm);

		for (int i = 0; i < entities.size(); i++) { // update accelerations
			Entity e = entities.get(i);
			VectorD2 a = am.get(e).vec;
			a.set(0f, 0f);
			tree.updateAcceleration(entities.get(i), theta, G, mm, pm, am);
		}

		time = System.nanoTime() - time;
//		System.out.println("grav: "+time*1e-6+" ms");
	}

	@Override
	protected void inserted(Entity e) {
		tree.add(e, mm, pm);
	}

	@Override
	protected void removed(Entity e) {
		tree.remove(e, mm, pm);
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

}
