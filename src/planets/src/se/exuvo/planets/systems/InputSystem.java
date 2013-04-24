package se.exuvo.planets.systems;

import se.exuvo.planets.EntityFactory;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Velocity;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.FastMath;
import com.artemis.utils.ImmutableBag;
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

public class InputSystem extends EntitySystem implements InputProcessor {
	@Mapper ComponentMapper<Size> sm;
	@Mapper ComponentMapper<Position> pm;

	private OrthographicCamera camera;
	private Vector3 mouseVector;
	private Vector2 mouseStartVector;

	private boolean createPlanet, releasePlanet, selectPlanet;
	private Entity lastPlanet, selectedPlanet;
	private ShapeRenderer render;

	public InputSystem(OrthographicCamera camera) {
		super(Aspect.getAspectForAll(Position.class, Size.class));
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
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		mouseVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mouseVector);

		if (selectPlanet) {
			selectedPlanet = null;
			Vector2 mouse = new Vector2().set(mouseVector.x, mouseVector.y);

			for (int i=0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				Position p = pm.get(e);
				Size s = sm.get(e);
				if (mouse.cpy().sub(p.vec).len2() < s.radius*s.radius) {
					selectedPlanet = e;
					break;
				}
			}

			selectPlanet = false;
		}

		if (selectedPlanet != null) {
			if (selectedPlanet.isActive()) {
				Position p = pm.get(selectedPlanet);
				Size s = sm.get(selectedPlanet);

				render.begin(ShapeType.Triangle);
				render.setColor(Color.CYAN);
				float r = s.radius * 2f;
				render.triangle(	p.vec.x + r * MathUtils.cosDeg(90), p.vec.y + r * MathUtils.sinDeg(90),
										p.vec.x + r * MathUtils.cosDeg(210), p.vec.y + r * MathUtils.sinDeg(210),
										p.vec.x + r * MathUtils.cosDeg(330), p.vec.y + r * MathUtils.sinDeg(330));
				render.end();
			} else {
				selectedPlanet = null;
			}
		}

		if (createPlanet) {
			mouseStartVector.set(mouseVector.x, mouseVector.y);
			lastPlanet = EntityFactory.createHollowPlanet(world, new Position(new Vector2(mouseVector.x, mouseVector.y)));
			lastPlanet.addToWorld();
			createPlanet = false;
		}

		if (lastPlanet != null) {
			float angle = MathUtils.atan2(mouseVector.x - mouseStartVector.x, mouseStartVector.y - mouseVector.y);

			Size size = sm.get(lastPlanet);
			float xr = size.radius * MathUtils.cos(angle);
			float yr = size.radius * MathUtils.sin(angle);

			render.begin(ShapeType.FilledTriangle);
			render.setColor(Color.CYAN);
			render.filledTriangle(	mouseStartVector.x + xr, mouseStartVector.y + yr, mouseStartVector.x - xr, mouseStartVector.y - yr,
									mouseVector.x, mouseVector.y);
			render.end();

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
		if (button == Input.Buttons.RIGHT) {
			createPlanet = true;
			return true;
		} else if (button == Input.Buttons.LEFT) {
			selectPlanet = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (button == Input.Buttons.RIGHT) {
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

	@Override
	protected boolean checkProcessing() {
		return true;
	}

}
