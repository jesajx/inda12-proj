package se.exuvo.planets.systems;

import se.exuvo.planets.components.Particle;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.utils.VectorD2;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ParticleSystem extends EntityProcessingSystem {
	@Mapper	ComponentMapper<Particle> pam;
	@Mapper	ComponentMapper<Position> pm;
	
	private SpriteBatch spriteBatch;
	private OrthographicCamera camera;

	public ParticleSystem(OrthographicCamera camera) {
		super(Aspect.getAspectForAll(Particle.class, Position.class));
		this.camera = camera;
	}
	
	@Override
	protected void initialize() {
		spriteBatch = new SpriteBatch();
	}

	@Override
	protected void begin() {
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
	}
	
	@Override
	protected void process(Entity e) {
		VectorD2 p = pm.get(e).vec;
		ParticleEffect effect = pam.get(e).effect;
		effect.setPosition(p.X(), p.Y());
		effect.draw(spriteBatch, Gdx.graphics.getDeltaTime());
		if(effect.isComplete()){
			e.deleteFromWorld();
		}
	}
	
	@Override
	protected void end() {
		spriteBatch.end();
	}
	
}
