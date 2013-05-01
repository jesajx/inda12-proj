package se.exuvo.planets;

import se.exuvo.planets.systems.AccelerationSystem;
import se.exuvo.planets.systems.GravitationSystem;
import se.exuvo.planets.systems.HudRenderSystem;
import se.exuvo.planets.systems.InputSystem;
import se.exuvo.planets.systems.PlanetRenderSystem;
import se.exuvo.planets.systems.UISystem;
import se.exuvo.planets.systems.VelocitySystem;
import se.exuvo.planets.utils.Settings;

import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * The game and main application screen. This class holds the contents of the gameloop.
 */
public class Planets extends Game implements Screen {
	private World world;
	private OrthographicCamera camera;

	private PlanetRenderSystem planetRenderSystem;
	private HudRenderSystem hudRenderSystem;
	private InputSystem inputSystem;
	private UISystem uiSystem;
	
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

		world.setSystem(inputSystem);
		world.setSystem(uiSystem, true);
		world.setSystem(new AccelerationSystem());
		world.setSystem(new VelocitySystem());
		world.setSystem(new GravitationSystem());

		planetRenderSystem = world.setSystem(new PlanetRenderSystem(camera), true);
		hudRenderSystem = world.setSystem(new HudRenderSystem(), true);

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
		EntityFactory.createPlanet(world, 10f, m, new Vector2(r, 0), new Vector2(0, -v), Color.WHITE).addToWorld();
		EntityFactory.createPlanet(world, 10f, m, new Vector2(-r, 0), new Vector2(0, v), Color.YELLOW).addToWorld();

		// these radii causes the game to crash.
//		// sun
//		float sun_radius = 1.392684e9f/2/1e8f; // m
//		float sun_mass = 1.9891e30f/1e30f; // kg
//		EntityFactory.createPlanet(world, sun_radius, sun_mass, new Vector2(0,0), new Vector2(0,0), Color.YELLOW).addToWorld();
//		
//		// earth
//		float earth_radius = 6371e3f/1e8f; // m
//		float earth_mass =  5.9376e24f/1e30f; // kg
//		float earth_aphelion = 152098232e3f;// m // 1.496e8f
//		float earth_avg_speed = 29.78e3f; // m/s
//		EntityFactory.createPlanet(world, earth_radius, earth_mass, new Vector2(earth_aphelion, 0), new Vector2(0, earth_avg_speed), Color.BLUE).addToWorld();
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
			for (int i = 0; 10 > i; i++) {
				world.process();
			}
		}
		world.process();

		planetRenderSystem.process();
		uiSystem.process();
		hudRenderSystem.process();
	}

	// --Screen--

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
