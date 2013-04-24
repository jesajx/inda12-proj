package se.exuvo.planets;

import se.exuvo.planets.systems.AccelerationSystem;
import se.exuvo.planets.systems.GravitationSystem;
import se.exuvo.planets.systems.HudRenderSystem;
import se.exuvo.planets.systems.InputSystem;
import se.exuvo.planets.systems.PlanetRenderSystem;
import se.exuvo.planets.systems.VelocitySystem;
import se.exuvo.planets.utils.Settings;

import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class Planets extends Game implements Screen {
	private World world;
	private OrthographicCamera camera;

	private PlanetRenderSystem planetRenderSystem;
	private HudRenderSystem hudRenderSystem;
	
	@Override
	public void create() {
		setScreen(this);
		this.camera = new OrthographicCamera(Settings.getInt("GUI.Width"), Settings.getInt("GUI.Height"));

		world = new World();

		world.setManager(new GroupManager());

		world.setSystem(new InputSystem(camera));
		world.setSystem(new AccelerationSystem());
		world.setSystem(new VelocitySystem());
		world.setSystem(new GravitationSystem());

		planetRenderSystem = world.setSystem(new PlanetRenderSystem(camera), true);
		hudRenderSystem = world.setSystem(new HudRenderSystem(camera), true);

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
		// 
		float m = 1e15f;
		float v = 0;//6.5f;
		float r = v*v*v;
		EntityFactory.createPlanet(world, 10f, m, new Vector2(r,0), new Vector2(0, -v), Color.WHITE).addToWorld();
		//EntityFactory.createPlanet(world, 10f, m, new Vector2(-r,0), new Vector2(0, v), Color.YELLOW).addToWorld();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();

		world.setDelta(delta);
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
			for (int i = 0; 10 > i; i++) {
				world.process();
			}
		}
		world.process();

		planetRenderSystem.process();
		hudRenderSystem.process();
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
