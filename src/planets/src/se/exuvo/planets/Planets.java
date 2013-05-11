package se.exuvo.planets;

import org.apache.log4j.Logger;

import se.exuvo.planets.components.Position;
import se.exuvo.planets.systems.AudioSystem;
import se.exuvo.planets.systems.HelpSystem;
import se.exuvo.planets.systems.PositionSystem;
import se.exuvo.planets.systems.TemplateUISystem;
import se.exuvo.planets.systems.VelocitySystem;
import se.exuvo.planets.systems.CollisionSystem;
import se.exuvo.planets.systems.GravitationSystem;
import se.exuvo.planets.systems.HudRenderSystem;
import se.exuvo.planets.systems.InputSystem;
import se.exuvo.planets.systems.ParticleSystem;
import se.exuvo.planets.systems.PlanetRenderSystem;
import se.exuvo.planets.systems.PrecognitionSystem;
import se.exuvo.planets.systems.UISystem;
import se.exuvo.settings.Settings;

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
	protected static final Logger log = Logger.getLogger(Planets.class);
	private World world;
	private OrthographicCamera camera;

	private InputSystem inputSystem;
	private UISystem uiSystem;
	private VelocitySystem accSystem;
	private GravitationSystem gravSystem;
	private CollisionSystem collSystem;
	private TemplateUISystem templateSystem;
	private HudRenderSystem hudSystem;
	private HelpSystem helpSystem;
	public float physicsInterval = Settings.getFloat("PhysicsStep"), accumulator;
	private final int physicsSkip = 10;

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
		templateSystem = new TemplateUISystem();
		helpSystem = new HelpSystem();

		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(helpSystem);
		multiplexer.addProcessor(templateSystem);
		multiplexer.addProcessor(uiSystem);
		multiplexer.addProcessor(inputSystem);
		Gdx.input.setInputProcessor(multiplexer);

		world.setSystem(gravSystem = new GravitationSystem(), true);
		world.setSystem(accSystem = new VelocitySystem(), true);
		world.setSystem(collSystem = new CollisionSystem(), true);
//		world.setSystem(new PositionSystem());
		world.setSystem(new PlanetRenderSystem(camera));
		world.setSystem(new ParticleSystem(camera));
		world.setSystem(new PrecognitionSystem(camera));
		world.setSystem(uiSystem);
		world.setSystem(inputSystem);
		world.setSystem(hudSystem = new HudRenderSystem(camera));
		world.setSystem(templateSystem);
		world.setSystem(helpSystem);
		world.setSystem(new AudioSystem(), true);

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

		EntityFactory.createParticleEffect(world).addToWorld();
	}

	/**
	 * The main part of the game loop. Processes all systems and renders the screen.
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		if (inputSystem.isSpeedup()) {
			if (inputSystem.isSSpeedup()) {
				speed(200, delta);
			} else {
				speed(10, delta);
			}
		} else if (inputSystem.isSSpeedup()) {
			speed(50, delta);
		} else {
			speed(1, delta);
		}

		camera.update();
		world.setDelta(delta);
		world.process();
	}

	private void speed(int multiplier, float delta) {
		accumulator += delta * multiplier;
		if (accumulator >= physicsInterval) {
			int stepsBehind = (int) (accumulator / physicsInterval);

			int step = Math.min(stepsBehind, physicsSkip);
			world.setDelta(physicsInterval * step * 10);

			System.out.println(stepsBehind + " " + step);

//			for (int i = 0; i < multiplier; i++) {
			gravSystem.process();
			accSystem.process();
			collSystem.process();
//			}

			accumulator -= step * physicsInterval;
		}
	}

	@Override
	public void resize(int width, int height) {
		if (camera != null) {
			camera.setToOrtho(false, width, height);
			uiSystem.resize(width, height);
			templateSystem.resize(width, height);
			hudSystem.resize(width, height);
		}
	}

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
