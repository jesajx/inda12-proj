package se.exuvo.planets.systems;

import se.exuvo.planets.components.Colour;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * EntittySystem responsible for drawing planets on the screen.
 */
public class PlanetRenderSystem extends EntityProcessingSystem {

	/** Mapper for entities with the Position-aspect. */
	@Mapper ComponentMapper<Position> pm;
	/** Mapper for entities with the Size-aspect. */
	@Mapper ComponentMapper<Size> sm;
	/** Mapper for entities with the Colour-aspect. */
	@Mapper ComponentMapper<Colour> cm;

	/** The gameworld camera. */
	private OrthographicCamera camera;

	/** Used to draw shaped (circles). */
	private ShapeRenderer render;

	public PlanetRenderSystem(OrthographicCamera camera) {
		super(Aspect.getAspectForAll(Position.class, Size.class));
		this.camera = camera;
	}

	@Override
	protected void initialize() {
		render = new ShapeRenderer();
	}

	@Override
	protected void begin() {
		render.setProjectionMatrix(camera.combined);
		render.begin(ShapeType.FilledCircle);
	}

	/**
	 * Draws the given Entity.
	 */
	@Override
	protected void process(Entity e) {
		// for each planet...

		// get relevant planet-components
		Position p = pm.get(e);
		Size s = sm.get(e);
		Colour c = cm.getSafe(e);

		if (c != null) {
			render.setColor(c.color);
		}
		// ...draw the planet.

		if (s.radius / camera.zoom < 0.5) {// Ensure planet is at least 1 pixel on screen
			render.filledCircle(p.vec.x, p.vec.y, camera.zoom, 3);
		} else {
			render.filledCircle(p.vec.x, p.vec.y, s.radius, (int) ((6 * (float) Math.cbrt(s.radius/ camera.zoom)) ));
		}
	}

	@Override
	protected void end() {
		render.end();
	}

}
