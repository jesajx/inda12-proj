package se.exuvo.planets.systems;

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
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * The system responsible for handling user input (keyboard and mouse).
 */
public class InputSystem extends EntitySystem implements InputProcessor {
	
	// --variables--
	/** Mapper for components with the Size-aspect. */
	@Mapper ComponentMapper<Size> sm;
	
	/** Mapper for compontents with the Position-aspect. */
	@Mapper ComponentMapper<Position> pm;
	
	/** The gameworld-camera. */
	private OrthographicCamera camera;
	/** Holds the mouse position during processing. */
	private Vector3 mouseVector; // TODO only used inside processEntities. move there completely?
	
	/**
	 * Used to hold the mouseposition where the user last placed a planet.
	 * It is used for when the user rightclick-drags the mouse,
	 * creating a planet at the rightclick and giving it an
	 * velocity in the dragged-to direction.
	 */
	private Vector2 mouseStartVector;

	// TODO generalize bool vars. i.e. call it something like RIGHT_MOUSE_DOWN rather than 'createPlanet'? i.e. let processing handle the logic, not the input.
	private boolean createPlanet, releasePlanet, selectPlanet;
	private Entity lastPlanet, selectedPlanet;
	private ShapeRenderer render;

	private boolean paused, wasPaused;
	
//	Stage ui; // TODO

	
	// --constructor--
	public InputSystem(OrthographicCamera camera) {
		super(Aspect.getAspectForAll(Position.class, Size.class));
		this.camera = camera;
		mouseVector = new Vector3();
		mouseStartVector = new Vector2();
		Gdx.input.setInputProcessor(this);
	}

	
	// --system--
	
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
		
		// TODO separate the various operations into methods.
		
		// mouse position on screen
		mouseVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		
		// update to corresponding mouse position in world
		camera.unproject(mouseVector);

		// if a planet is TO BE selected
		if (selectPlanet) { // TODO perhaps clearer with a "LEFT_MOUSE_CLICKED"-variable?
			
			// unselect any already selected planet. (the user might have clicked between planets)
			selectedPlanet = null;
			
			// convert 3D mousePos to 2D
			Vector2 mouse = new Vector2().set(mouseVector.x, mouseVector.y);

			// TODO would it be better to let the planets themselves check if they were clicked? that might perhaps not be artemis-style though.
			// compare each planet to the mousePos to see if it was clicked.
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				Position p = pm.get(e);
				Size s = sm.get(e);
				if (mouse.dst2(p.vec) < s.radius * s.radius) {
					selectedPlanet = e;
					break;
				}
			}

			// planet has been selected. (so don't redo it)
			selectPlanet = false;
		}

		// if a planets IS selectd 
		if (selectedPlanet != null) {
			
			// if the planet hasn't died or something.
			if (selectedPlanet.isActive()) {
				Position p = pm.get(selectedPlanet);
				Size s = sm.get(selectedPlanet);
				
				// draw a triangle around the planet (showing that it's selected)
				render.begin(ShapeType.Triangle);
				render.setColor(Color.CYAN);
				float r = s.radius * 2f;
				render.triangle(p.vec.x + r * MathUtils.cosDeg(90), p.vec.y + r * MathUtils.sinDeg(90),
								p.vec.x + r * MathUtils.cosDeg(210), p.vec.y + r * MathUtils.sinDeg(210),
								p.vec.x + r * MathUtils.cosDeg(330), p.vec.y + r * MathUtils.sinDeg(330));
				render.end();
			} else {
				// unselect "dead" planet.
				selectedPlanet = null;
			}
		}

		// if a new planet should be created.
		if (createPlanet) {
			// remember we placed the planet. (incase the user then drags)
			mouseStartVector.set(mouseVector.x, mouseVector.y);
			// add new (random) planet to the world.
			lastPlanet = EntityFactory.createHollowPlanet(world, new Position(new Vector2(mouseVector.x, mouseVector.y)));
			lastPlanet.addToWorld();
			
			wasPaused = paused;
			if(Settings.getBol("pauseWhenCreatingPlanets")){
				setPaused(true);
			}
			
			// the planet has been created (so don't redo it)
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

			// if the right mouse was released ("fly, planet!")
			if (releasePlanet) {
				// give the planet a velocity. (with the angle and magnitude the user showed)
				EntityFactory.fillPlanet(lastPlanet, new Velocity(new Vector2(mouseVector.x - mouseStartVector.x, mouseVector.y
						- mouseStartVector.y).div(10f)));
				
				// release the planet from processing
				lastPlanet = null;
				releasePlanet = false; // (and don't re-release it)
				
				if(!wasPaused){
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

	private void setPaused(boolean newValue){
		paused = newValue;
		world.getSystem(VelocitySystem.class).setPaused(paused);
		// TODO other systems?
	}
	
	
	// --input--
	
	@Override
	public boolean keyDown(int keycode) {
//	    if (ui.keyDown(keycode)) {
//	        return true;
//	    }
		if (keycode == Input.Keys.SPACE) {
			setPaused(!paused);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
//	    if (ui.keyUp(keycode)) {
//	        return true;
//	    }
		return false;
	}

	@Override
	public boolean keyTyped(char c) {
//	    if (ui.keyTyped(c)) {
//	        return true;
//	    }
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
//	    if (ui.touchDown(x, y, pointer, button)) {
//	        return true;
//	    }
		// TODO use something like a bitmap to handle many inputs? if map.contains(input) /*could be in map but mapped to false*/ then map.put(input, true)
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
//	    if (ui.touchUp(x, y, pointer, button)) {
//	        return true;
//	    }
		if (button == Input.Buttons.RIGHT) {
			releasePlanet = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
//	    if (ui.touchDragged(x, y, pointer)) {
//	        return true;
//	    }
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
//	    if (ui.scrolled(amount)) {
//	        return true;
//	    }
		// forward-scroll makes amount negative.
		camera.zoom += amount;
		if (camera.zoom < 1) {
			camera.zoom = 1;
		}
		
		// DEBUG
//		System.out.println("zoom: "+camera.zoom);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
//	    if (ui.mouseMoved(screenX, screenY)) {
//	        return true;
//	    }
		return false;
	}
}
