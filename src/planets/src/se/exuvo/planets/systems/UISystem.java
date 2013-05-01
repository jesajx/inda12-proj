package se.exuvo.planets.systems;

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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.tablelayout.BaseTableLayout.Debug;

/**
 * The system responsible for handling user input (keyboard and mouse).
 */
public class UISystem extends VoidEntitySystem implements InputProcessor {

	private Stage ui;
	private Window table;
	private static int debug = 0;

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
	private Stage createUI() {
		int height = Gdx.graphics.getHeight(), width = Gdx.graphics.getWidth();

		Stage stage = new Stage();
		Skin skin = new Skin(Gdx.files.internal("resources/uiskin.json"));

		 table = new Window("magic here", skin);
		table.align(Align.center | Align.top);
		table.setSize(200, height);
		table.setPosition(-width / 2, -height / 2);
		stage.addActor(table);

		// Massa hastighet xy accel xy volym

		addField("Mass", table, skin);
		addField("Size", table, skin);
		addField2("Velocity", table, skin);
		addField2("Acceleration", table, skin);

//		TextButton button = new TextButton("Click me!", skin);
//		button.addListener(new ChangeListener() {
//			@Override
//			public void changed(ChangeEvent event, Actor actor) {
//				// TODO Auto-generated method stub
//				System.out.println("clicked me! " + actor);
//			}
//		});
//		table.addActor(button);

		return stage;
	}

	private TextField addField(String name, Table table, Skin skin) {
		Label label = new Label(name, skin);
		TextField field = new TextField("", skin);
		table.add(label);
		table.row();
		table.add(field).expandX().fillX();
		table.row();
		return field;
	}

	private TextFields addField2(String name, Table rootTable, Skin skin) {
		Table table = new Table(skin);
		rootTable.add(table).expandX().fillX().row();
//		table.debugWidget();

		Label label = new Label(name, skin);
		Label labelX = new Label("X", skin);
		Label labelY = new Label("Y", skin);
		TextFields fields = new TextFields();
		fields.a = new TextField("", skin);
		fields.b = new TextField("", skin);

		table.add(label).colspan(2);
		table.row();
		table.add(labelX).padRight(10).padLeft(5);
		table.add(fields.a).expandX().fillX();
		table.row();
		table.add(labelY).padRight(10).padLeft(5);;
		table.add(fields.b).fillX();
		table.row();
		return fields;
	}

	private static class TextFields {
		TextField a, b;
	}

	@Override
	protected void end() {

	}

	// --input--
	
	private void d(){
		switch(debug){
			case 1:
				table.debugTable();
				break;
			case 2:
				table.debugCell();
				break;
			case 3:
				table.debugWidget();
				break;
			case 4:
				table.debug();
				break;
			default:
				table.debug(Debug.none);
		}
		table.invalidate();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (ui.keyDown(keycode)) {
			return true;
		}
		if(keycode == Input.Keys.A){
			debug = (debug+1) % 5;
			d();
		}
		if(keycode == Input.Keys.Z){
			debug--;
			if(debug < 0) debug = 4;
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
}
