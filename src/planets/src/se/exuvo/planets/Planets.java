package se.exuvo.planets;

import se.exuvo.planets.systems.AccelerationSystem;
import se.exuvo.planets.systems.CollisionSystem;
import se.exuvo.planets.systems.GravitationSystem;
import se.exuvo.planets.systems.HudRenderSystem;
import se.exuvo.planets.systems.InputSystem;
import se.exuvo.planets.systems.ParticleSystem;
import se.exuvo.planets.systems.PlanetRenderSystem;
import se.exuvo.planets.systems.PrecognitionSystem;
import se.exuvo.planets.systems.UISystem;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

/**
 * The game and main application screen. This class holds the contents of the gameloop.
 */
public class Planets extends Game implements Screen {
	private World world;
	private OrthographicCamera camera;

	private InputSystem inputSystem;
	private UISystem uiSystem;
	private AccelerationSystem accSystem;
	private GravitationSystem gravSystem;
	private CollisionSystem collSystem;

	/**
	 * Initializes the game.
	 */
	@Override
	public void create() {
		setScreen(this);
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		world = new World();

		world.setManager(new GroupManager());

		inputSystem = new InputSystem(camera);
		uiSystem = new UISystem();

		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(uiSystem);
		multiplexer.addProcessor(inputSystem);
		Gdx.input.setInputProcessor(multiplexer);

		world.setSystem(accSystem = new AccelerationSystem());
		world.setSystem(gravSystem = new GravitationSystem());
		world.setSystem(collSystem = new CollisionSystem());
		world.setSystem(new PlanetRenderSystem(camera));
		world.setSystem(new ParticleSystem(camera));
		world.setSystem(new PrecognitionSystem(camera));
		world.setSystem(uiSystem);
		world.setSystem(inputSystem);
		world.setSystem(new HudRenderSystem());

		world.initialize();

		// F=G*m*M/d^2
		// F=ma
		// a = G*M/d^2

		// a = v^2/r
		// v^2 = a*r
		// v^2 = G*M/r
		// r =a*v^2

		// F=m*v^2/r^2
		// v^2 = F*r^2/m
		// v^2 = a*r^2
		// v^2 = G*M

		float m = 1e16f;// 1e15f;
		float v = 10f;// 6.f;
		float r = 10 * 6 * 6 * 6;// v*v*v;
//		EntityFactory.createPlanet(world, 50f, m, new Vector2(r, 0), new Vector2(0, -v), Color.WHITE).addToWorld();
//		EntityFactory.createPlanet(world, 50f, m, new Vector2(-r, 0), new Vector2(0, v), Color.YELLOW).addToWorld();
		int n = 50;
		int i = 1000;
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < n; y++) {
				EntityFactory.createPlanet(world, i, 1e17f, new Vector2(10*i*0, 10*i*0), new Vector2((float) Math.random(),(float) Math.random()).mul(100f), Color.YELLOW).addToWorld();
			}
		}

		// these radii causes the game to crash.
//		// sun
//		float sun_radius = 1.392684e9f/2/1e8f; // m
//		float sun_mass = 1.9891e30f/1e30f; // kg
//		EntityFactory.createPlanet(world, sun_radius, sun_mass, new Vector2(), new Vector2(), Color.YELLOW).addToWorld();
//		
//		// earth
//		float earth_radius = 6371e3f/1e8f; // m
//		float earth_mass =  5.9376e24f/1e30f; // kg
//		float earth_aphelion = 152098232e3f;// m // 1.496e8f
//		float earth_avg_speed = 29.78e3f; // m/s
//		EntityFactory.createPlanet(world, earth_radius, earth_mass, new Vector2(earth_aphelion, 0), new Vector2(0, earth_avg_speed), Color.BLUE).addToWorld();

		EntityFactory.createParticleEffect(world).addToWorld();
	}

	/**
	 * The main part of the game loop. Processes all systems and renders the screen.
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		world.setDelta(delta);
		
		if (inputSystem.isSpeedup()) {
			if (inputSystem.isSSpeedup()) {
				speed(200);
			} else {
				speed(10);
			}
		} else if (inputSystem.isSSpeedup()) {
			speed(50);
		}
		
		world.process();
	}

	private void speed(int multiplier) {
		for (int i = 1; i < multiplier; i++) {
			gravSystem.process(); // update acc
			accSystem.process(); // update vel
			collSystem.process(); // update pos
		}
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}

}
