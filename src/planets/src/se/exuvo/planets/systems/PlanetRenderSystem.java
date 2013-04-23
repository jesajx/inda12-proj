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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class PlanetRenderSystem extends EntityProcessingSystem {
	
	@Mapper	ComponentMapper<Position> pm;
	@Mapper	ComponentMapper<Size> sm;
	@Mapper	ComponentMapper<Colour> cm;

	private OrthographicCamera camera;
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

	@Override
	protected void process(Entity e) {
		Position p = pm.get(e);
		Size s = sm.get(e);
		Colour c = cm.getSafe(e);
		
		if(c != null){
			render.setColor(c.color);
		}
		
		render.filledCircle(p.vec.x, p.vec.y, s.radius);
	}

	@Override
	protected void end() {
		render.end();
	}

}
