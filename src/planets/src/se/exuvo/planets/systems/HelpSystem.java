package se.exuvo.planets.systems;

import se.exuvo.planets.templates.TemplateLoader;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.tablelayout.BaseTableLayout.Debug;

/**
 */
public class HelpSystem extends VoidEntitySystem implements InputProcessor {

	private Stage ui;
	private Window window;
	private static int debug = 0;

	public HelpSystem() {}

	@Override
	protected void initialize() {
		TemplateLoader.init();
		ui = createUI();
		hide();
	}

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
	private Stage createUI() {
		int height = Math.min(Gdx.graphics.getHeight(), 400), width = Math.min(Gdx.graphics.getWidth(), 400);
		int realHeight = Gdx.graphics.getHeight(), realWidth = Gdx.graphics.getWidth();

		Stage stage = new Stage();
		Skin skin = new Skin(Gdx.files.internal("resources/uiskin.json"));

		window = new Window("Controls", skin);
		stage.addActor(window);

		addHelpRow("Keys", "Description", window, skin);
		addHelpRow("Right Mouse", "Create planet", window, skin);
		addHelpRow("Right Mouse + drag", "Create and push planet", window, skin);
		addHelpRow("Left Mouse", "Select Planets", window, skin);
		addHelpRow("Left Mouse + shift", "Select multiple", window, skin);
		addHelpRow("Left Mouse over planet + drag", "Move planet", window, skin);
		addHelpRow("Left Mouse + shift + drag", "Move selected planets", window, skin);
		addHelpRow("T", "track selected planets", window, skin);
		addHelpRow("N", "Goto and select next planet", window, skin);
		addHelpRow("CTRL", "Speed x10", window, skin);
		addHelpRow("ALT", "Speed x50", window, skin);
		addHelpRow("CTRL + ALT", "Speed x200", window, skin);
		addHelpRow("", "", window, skin);

		window.row();
		TextButton back = new TextButton("Back", skin);
		window.add(back).spaceTop(10);
		back.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				hide();
			}
		});

		window.pack();
		window.setPosition((realWidth - window.getWidth()) / 2, (realHeight - window.getHeight()) / 2);

		return stage;
	}

	private void addHelpRow(String key, String description, Table window, Skin skin) {
		Label keyL = new Label(key, skin);
		Label descL = new Label(description, skin);

		Table table = new Table();
		table.add(keyL).left();
		table.add(new Label("", skin)).spaceRight(20).spaceLeft(20).expandX();
		table.add(descL).right();

		window.add(table).expandX().fillX().row();
	}

	@Override
	protected void end() {

	}

	public void hide() {
		window.setVisible(false);
		ui.unfocusAll();
	}

	public void show() {
		window.setVisible(true);
	}

	private void d() {
		switch (debug) {
			case 1:
				window.debugTable();
				break;
			case 2:
				window.debugCell();
				break;
			case 3:
				window.debugWidget();
				break;
			case 4:
				window.debug();
				break;
			default:
				window.debug(Debug.none);
		}
		window.invalidate();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (ui.keyDown(keycode)) {
			return true;
		}
		if (keycode == Input.Keys.H) {
			show();
			return true;
		}
		if (keycode == Input.Keys.Z) {
			debug = (debug + 1) % 5;
			d();
		}
		if (keycode == Input.Keys.X) {
			debug--;
			if (debug < 0) debug = 4;
			d();
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

	public void resize(int width, int height) {
		ui.setViewport(width, height, false);
	}

}