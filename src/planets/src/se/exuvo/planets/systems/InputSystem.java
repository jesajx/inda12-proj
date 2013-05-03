package se.exuvo.planets.systems;

import java.util.ArrayList;
import java.util.List;

import se.exuvo.planets.EntityFactory;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Velocity;
import se.exuvo.settings.Settings;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
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

/**
 * The system responsible for handling user input (keyboard and mouse).
 */
public class InputSystem extends EntitySystem implements InputProcessor {

	@Mapper ComponentMapper<Size> sm;
	@Mapper ComponentMapper<Position> pm;
	@Mapper ComponentMapper<Velocity> vm;

	/** The gameworld-camera. */
	private OrthographicCamera camera;
	private Vector3 cameraVelocity;
	private float cameraMoveSpeed = 1000f;

	private Vector2 mouseVector;

	/**
	 * Used to hold the mouseposition where the user last placed a planet. It is used for when the user rightclick-drags the mouse, creating
	 * a planet at the rightclick and giving it an velocity in the dragged-to direction.
	 */
	private Vector2 mouseStartVector;

	// Buffers due to gui has to be done in the correct thread.
	private boolean createPlanet, releasePlanet, selectPlanet;
	private Entity lastPlanet, selectedPlanet;
	private ShapeRenderer render;

	private boolean paused, wasPaused;

	private List<PlanetSelectionChanged> listeners = new ArrayList<PlanetSelectionChanged>();
	private UISystem uisystem;

	public InputSystem(OrthographicCamera camera) {
		super(Aspect.getAspectForAll(Position.class, Size.class));
		this.camera = camera;
		mouseVector = new Vector2();
		mouseStartVector = new Vector2();
		cameraVelocity = new Vector3();
	}

	@Override
	protected void initialize() {
		render = new ShapeRenderer();
		uisystem = world.getSystem(UISystem.class);
	}

	@Override
	protected void begin() {
		render.setProjectionMatrix(camera.combined);
	}

	private void updateMouse() {
		Vector3 mouseTmp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

		// unproject screen coordinates to corresponding world position
		camera.unproject(mouseTmp);
		mouseVector.set(mouseTmp.x, mouseTmp.y);
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		// TODO separate the various operations into methods.
	    camera.position.add(cameraVelocity.cpy().mul(camera.zoom*Gdx.graphics.getDeltaTime()));
		updateMouse();

		if (selectPlanet) {
			selectedPlanet = null; // unselect any already selected planet. (the user might have clicked between planets)

			// convert 3D mousePos to 2D
			Vector2 mouse = new Vector2().set(mouseVector.x, mouseVector.y);

			// TODO would it be better to let the planets themselves check if they were clicked? that might perhaps not be artemis-style
// though.
			// compare each planets position to the mousePos to see if it was clicked.
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				Position p = pm.get(e);
				Size s = sm.get(e);
				if (mouse.dst2(p.vec) < s.radius * s.radius) {
					selectedPlanet = e;
					break;
				}
			}

			fireSelectionChangeEvent();
			selectPlanet = false;
		}

		if (selectedPlanet != null) {

			// if the planet hasn't died or something.
			if (selectedPlanet.isActive()) {
				Position p = pm.get(selectedPlanet);
				Size s = sm.get(selectedPlanet);

				// draw a triangle around the planet (showing that it's selected)
				render.begin(ShapeType.Triangle);
				render.setColor(Color.CYAN);
				float r = s.radius * 3f;
				render.triangle(p.vec.x + r * MathUtils.cosDeg(90), p.vec.y + r * MathUtils.sinDeg(90),
								p.vec.x + r * MathUtils.cosDeg(210), p.vec.y + r * MathUtils.sinDeg(210),
								p.vec.x + r * MathUtils.cosDeg(330), p.vec.y + r * MathUtils.sinDeg(330));
				render.end();
			} else {
				selectedPlanet = null;
				fireSelectionChangeEvent();
			}
		}

		if (createPlanet) {
			mouseStartVector.set(mouseVector.x, mouseVector.y);
			if (selectedPlanet != null) {
				lastPlanet = selectedPlanet;
			} else {
				lastPlanet = EntityFactory.createHollowPlanet(world, uisystem.getRadius(), uisystem.getMass(), new Vector2(mouseVector.x,
						mouseVector.y), uisystem.getColor());
				lastPlanet.addToWorld();
			}

			wasPaused = paused;
			if (Settings.getBol("pauseWhenCreatingPlanets")) {
				setPaused(true);
			}

			createPlanet = false;
		}

		// if the user dragged the mouse in a direction after creating a planet.
		if (lastPlanet != null) {

			// from where the planet was created (old mousePos) to the current mousePos
			float angle = MathUtils.atan2(mouseVector.x - mouseStartVector.x, mouseStartVector.y - mouseVector.y);

			Size size = sm.get(lastPlanet);
			float xr = size.radius * MathUtils.cos(angle);
			float yr = size.radius * MathUtils.sin(angle);

			// draw an arrow-like triangle from the planet to the current mousePos
			render.begin(ShapeType.FilledTriangle);
			render.setColor(Color.CYAN);
			render.filledTriangle(	mouseStartVector.x + xr, mouseStartVector.y + yr, mouseStartVector.x - xr, mouseStartVector.y - yr,
									mouseVector.x, mouseVector.y);
			render.end();

			if (releasePlanet) {
				// give the planet a velocity. (with the angle and magnitude the user showed)
				Vector2 push = new Vector2(mouseVector.x - mouseStartVector.x, mouseVector.y - mouseStartVector.y).div(10f);

				if (selectedPlanet != null) {
					vm.get(selectedPlanet).vec.add(push);
				} else {
					EntityFactory.fillPlanet(lastPlanet, uisystem.getVelocity().add(push));
				}
				lastPlanet = null;
				releasePlanet = false;

				if (!wasPaused) {
					setPaused(false);
				}
			}
		}
	}

	@Override
	protected void end() {

	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}

	public boolean isPaused() {
		return paused;
	}

	private void setPaused(boolean newValue) {
		paused = newValue;
	}

	public boolean isSpeedup() {
		return !isPaused() && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.SPACE) {
			setPaused(!paused);
			return true;
		} else if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
		    cameraVelocity.y += cameraMoveSpeed;
		    return true;
		} else if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
		    cameraVelocity.y += -cameraMoveSpeed;
		    return true;
		} else if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
		    cameraVelocity.x += cameraMoveSpeed;
		    return true;
		} else if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
		    cameraVelocity.x += -cameraMoveSpeed;
		    return true;
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
	    if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
		    cameraVelocity.y -= cameraMoveSpeed;
		    return true;
		} else if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
		    cameraVelocity.y -= -cameraMoveSpeed;
		    return true;
		} else if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
		    cameraVelocity.x -= cameraMoveSpeed;
		    return true;
		} else if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
		    cameraVelocity.x -= -cameraMoveSpeed;
		    return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char c) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		// TODO use something like a bitmap to handle many inputs? if map.contains(input) /*could be in map but mapped to false*/ then
// map.put(input, true)
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
		// forward-scroll makes amount negative.
		float oldZoom = camera.zoom;

		camera.zoom += amount*amount*amount;
		if (camera.zoom < 1) {
			camera.zoom = 1;
		}

		if (amount < 0) {
//			Det som var under musen innan scroll ska fortsätta vara där efter zoom
//			http://stackoverflow.com/questions/932141/zooming-an-object-based-on-mouse-position

			Vector3 diff = camera.position.cpy().sub(new Vector3(mouseVector.x, mouseVector.y, 0));
			camera.position.sub(diff.sub(diff.cpy().div(oldZoom).mul(camera.zoom)));
		}

//		System.out.println("zoom: "+camera.zoom);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	public void addListener(PlanetSelectionChanged psc) {
		listeners.add(psc);
	}

	private void fireSelectionChangeEvent() {
		for (PlanetSelectionChanged psc : listeners) {
			psc.planetSelectionChanged(selectedPlanet);
		}
	}

	public static interface PlanetSelectionChanged {
		public void planetSelectionChanged(Entity planet);
	}
}
