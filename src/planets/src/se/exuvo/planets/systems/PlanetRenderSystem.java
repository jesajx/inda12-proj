package se.exuvo.planets.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlanetRenderSystem extends EntityProcessingSystem {

	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	public PlanetRenderSystem(OrthographicCamera camera) {
		super(Aspect.getEmpty());
		this.camera = camera;
	}
	
	@Override
	protected void initialize() {
		batch = new SpriteBatch();
	}
	
	@Override
	protected void begin() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
	}

	@Override
	protected void process(Entity e) {
	}
	
	@Override
	protected void end() {
		batch.end();
	}

}
