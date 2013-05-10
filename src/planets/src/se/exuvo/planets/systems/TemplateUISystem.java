package se.exuvo.planets.systems;

import java.util.List;

import se.exuvo.planets.templates.Template;
import se.exuvo.planets.templates.TemplateLoader;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.tablelayout.BaseTableLayout.Debug;

/**
 */
public class TemplateUISystem extends VoidEntitySystem implements InputProcessor {

	private Stage ui;
	private Window window;
	private com.badlogic.gdx.scenes.scene2d.ui.List templateList;
	private Label name, description;
	private static int debug = 0;

	public TemplateUISystem() {
	}

	@Override
	protected void initialize() {
		TemplateLoader.init();
		ui = createUI();
		updateSelection();
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

		window = new Window("Planet Parameters", skin);
		window.setSize(width, height);
		window.setPosition((realWidth - width) / 2, (realHeight - height) / 2);
		stage.addActor(window);

		name = new Label("", skin);
		description = new Label("", skin);
		
		Table current = new Table(skin);
		current.add(name).center().expandX().padBottom(20).row();
		current.add(description).center().expandX().row();

		List<Template> templates = TemplateLoader.getTemplates();
		templateList = new com.badlogic.gdx.scenes.scene2d.ui.List(templates.toArray(), skin);
		templateList.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				updateSelection();
			}
		});
		
		ScrollPane sp = new ScrollPane(templateList, skin);
		window.add(sp).expand().fill().prefWidth(width/2);
		window.add(current).expand().top().padTop(20).prefWidth(width/2);

		window.row();
		TextButton back = new TextButton("Back", skin);
		window.add(back).spaceTop(10);
		back.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				hide();
			}
		});
		
		TextButton load = new TextButton("Load", skin);
		window.add(load).spaceTop(10);
		load.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				TemplateLoader.loadTemplate(TemplateLoader.getTemplates().get(templateList.getSelectedIndex()), world);
				world.getSystem(AudioSystem.class).playLoad();
				hide();
			}
		});
		
		return stage;
	}

	@Override
	protected void end() {

	}
	
	private void updateSelection(){
		Template temp = TemplateLoader.getTemplates().get(templateList.getSelectedIndex());
		name.setText(temp.getName());
		description.setText(temp.getDescription());
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
		if (keycode == Input.Keys.A) {
			debug = (debug + 1) % 5;
			d();
		}
		if (keycode == Input.Keys.Z) {
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