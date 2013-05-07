package se.exuvo.planets.systems;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Velocity;
import se.exuvo.planets.systems.InputSystem.PlanetSelectionChanged;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class PrecognitionSystem extends IntervalEntitySystem implements PlanetSelectionChanged {

	@Mapper ComponentMapper<Position> pm;
	@Mapper ComponentMapper<Acceleration> am;
	@Mapper ComponentMapper<Mass> mm;
	@Mapper ComponentMapper<Velocity> vm;

	private OrthographicCamera camera;
	private ShapeRenderer render;

	// must be multiple of 2 for drawing to work correctly
	private static int forwardComputationSteps = 100;
	private Vector2[] futureSteps = new Vector2[forwardComputationSteps];
	private Entity selectedPlanet, selectedFuture;

	private World futureWorld;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Future<?> task;

	public PrecognitionSystem(OrthographicCamera camera) {
		super(Aspect.getAspectForAll(Mass.class, Acceleration.class, Position.class, Velocity.class), Settings.getFloat("PhysicsStep")
				* forwardComputationSteps);
		this.camera = camera;
	}

	@Override
	protected void initialize() {
		render = new ShapeRenderer();

		futureWorld = new World();

		futureWorld.setSystem(new GravitationSystem());
		futureWorld.setSystem(new AccelerationSystem());
//		futureWorld.setSystem(new VelocitySystem());
		futureWorld.setSystem(new CollisionSystem());

		futureWorld.initialize();
		futureWorld.setDelta(Settings.getFloat("PhysicsStep"));

		world.getSystem(InputSystem.class).addListener(this);
	}

	@Override
	protected void begin() {
		// initialize rendering
		render.setProjectionMatrix(camera.combined);
		render.begin(ShapeType.Line);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		// draw between dots
		for (int i = 0; i < futureSteps.length; i += 2) {
			// TODO avoid possible concurrent read write
			Vector2 p1 = futureSteps[i];
			Vector2 p2 = futureSteps[i + 1];

			if (p1 == null || p2 == null) {
				break;
			}

			render.line(p1.x, p1.y, p2.x, p2.y);
		}

		// copy world each interval
		refreshFuture(entities);
	}

	@Override
	protected void end() {
		render.end();
	}

	private void clearWorld() {
		int toDelete = futureWorld.getEntityManager().getActiveEntityCount();
		int max = (int) futureWorld.getEntityManager().getTotalCreated();
		for (int i = 0; i < max; i++) {
			Entity e = futureWorld.getEntity(i);
			if (e != null) {
				futureWorld.deleteEntity(e);
				toDelete--;
				if (toDelete == 0) break;
			}
		}
	}

	private void copyWorld(ImmutableBag<Entity> entities) {
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);

			Position p = pm.get(e);
			Mass m = mm.get(e);
			Velocity v = vm.get(e);
			Acceleration a = am.get(e);

			// Copy entity
			Entity eCopy = futureWorld.createEntity();
			eCopy.addComponent(p.clone());
			eCopy.addComponent(m.clone());
			eCopy.addComponent(v.clone());
			eCopy.addComponent(a.clone());

			futureWorld.addEntity(eCopy);

			if (e == selectedPlanet) {
				selectedFuture = eCopy;
			}
		}
	}

	private void stopTask() {
		if (task != null) {
			if (!task.isDone()) {
				task.cancel(true);
			}
		}
	}

	private void startTask() {
		task = executor.submit(new Runnable() {
			@Override
			public void run() {
				System.out.println("work");
				ComponentMapper<Position> futurePM = futureWorld.getMapper(Position.class);

				for (int i = 0; i < forwardComputationSteps; i++) {
					if (Thread.interrupted()) {
						System.out.println("Interrupted");
						break;
					}
					futureWorld.process();

					futureSteps[i] = futurePM.get(selectedFuture).vec.cpy();
				}
			}
		});
	}

	private void refreshFuture(ImmutableBag<Entity> entities) {
		stopTask();
		clearWorld();
		if (selectedPlanet != null) {
			copyWorld(entities);
			startTask();
		}
	}

	@Override
	public void planetSelectionChanged(Entity planet) {
		selectedPlanet = planet;
		stopTask();
	}

}
