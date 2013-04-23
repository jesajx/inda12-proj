package se.exuvo.planets.systems;

import se.exuvo.planets.EntityFactory;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Velocity;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.FastMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class InputSystem extends VoidEntitySystem implements InputProcessor {
	@Mapper ComponentMapper<Size> sm;

	private OrthographicCamera camera;
	private Vector3 mouseVector;
	private Vector2 mouseStartVector;

	private boolean createPlanet, releasePlanet;
	private Entity lastPlanet;
	private ShapeRenderer render;

	public InputSystem(OrthographicCamera camera) {
		this.camera = camera;
		mouseVector = new Vector3();
		mouseStartVector = new Vector2();
		Gdx.input.setInputProcessor(this);
	}

	@Override
	protected void initialize() {
		render = new ShapeRenderer();
	}

	@Override
	protected void begin() {
		render.setProjectionMatrix(camera.combined);
		render.begin(ShapeType.FilledTriangle);
	}

	@Override
	protected void processSystem() {
		mouseVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mouseVector);

		if (createPlanet) {
			mouseStartVector.set(mouseVector.x, mouseVector.y);
			lastPlanet = EntityFactory.createHollowPlanet(world, new Position(new Vector2(mouseVector.x, mouseVector.y)));
			lastPlanet.addToWorld();
			createPlanet = false;
		}

		if (lastPlanet != null) {
			float angle = MathUtils.atan2(mouseVector.x - mouseStartVector.x, mouseStartVector.y - mouseVector.y);

			Size size = sm.get(lastPlanet);
			System.out.println(-MathUtils.sin(angle));
			float xr = size.radius * MathUtils.cos(angle);
			float yr = size.radius * MathUtils.sin(angle);

			render.setColor(Color.CYAN);
			render.filledTriangle(	mouseStartVector.x + xr, mouseStartVector.y + yr, mouseStartVector.x - xr, mouseStartVector.y - yr,
									mouseVector.x, mouseVector.y);

			if (releasePlanet) {
				EntityFactory.fillPlanet(lastPlanet, new Velocity(new Vector2(mouseVector.x - mouseStartVector.x, mouseVector.y
						- mouseStartVector.y).div(10f)));
				lastPlanet = null;
				releasePlanet = false;
			}
		}
	}

	@Override
	protected void end() {
		render.end();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			createPlanet = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			releasePlanet = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

}
