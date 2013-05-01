package se.exuvo.planets.components;

import com.artemis.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Holds a particle effect. Usually for a planet collision.
 */
public class Particle extends Component {
	public ParticleEffect effect;

	public Particle() {
		this(new ParticleEffect());
		TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("resources/particles.atlas"));
		effect.load(Gdx.files.internal("resources/src/particles/test.p"), textureAtlas);
		effect.setDuration(1000);
		effect.start();
	}

	public Particle(ParticleEffect initialEffect) {
		effect = initialEffect;
	}
}
