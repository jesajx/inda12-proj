package se.exuvo.planets.systems;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * The system responsible for handling user input (keyboard and mouse).
 */
public class UISystem extends VoidEntitySystem implements InputProcessor {

	private Stage ui;

	public UISystem() {
		ui = createUI();
	}

	@Override
	protected void initialize() {}

	@Override
	protected void begin() {}

	@Override
	protected void processSystem() {
		ui.act(Gdx.graphics.getDeltaTime());
		ui.draw();
		Table.drawDebug(ui);
	}

	/**
	 * Creates the UI-layer of the game. TODO It creates an Menu on the left side of the screen and an "clock" in the bottom middle of the
	 * screen.
	 * 
	 * @return the ui
	 */
	private static Stage createUI() {
		Stage stage = new Stage();
		Skin skin = new Skin(Gdx.files.internal("resources/uiskin.json"));
		
		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);

		TextButton button = new TextButton("Click me!", skin);
		button.setX(0);
		button.setY(0);
		button.addListener(new EventListener() {
			@Override
			public boolean handle(Event event) {
				// TODO Auto-generated method stub
				System.out.println("clicked me!");
				return false;
			}
		});
		table.addActor(button);

		return stage;
	}

	@Override
	protected void end() {

	}

	// --input--

	@Override
	public boolean keyDown(int keycode) {
		if (ui.keyDown(keycode)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (ui.keyUp(keycode)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char c) {
		if (ui.keyTyped(c)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if (ui.touchDown(x, y, pointer, button)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (ui.touchUp(x, y, pointer, button)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		if (ui.touchDragged(x, y, pointer)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (ui.scrolled(amount)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (ui.mouseMoved(screenX, screenY)) {
			return true;
		}
		return false;
	}
}
