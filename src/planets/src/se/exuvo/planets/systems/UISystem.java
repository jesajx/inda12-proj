package se.exuvo.planets.systems;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Colour;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Velocity;
import se.exuvo.planets.systems.InputSystem.PlanetSelectionChanged;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.esotericsoftware.tablelayout.BaseTableLayout.Debug;

/**
 */
public class UISystem extends VoidEntitySystem implements InputProcessor, PlanetSelectionChanged {
	@Mapper ComponentMapper<Size> sm;
	@Mapper ComponentMapper<Position> pm;
	@Mapper ComponentMapper<Colour> cm;
	@Mapper ComponentMapper<Acceleration> am;
	@Mapper ComponentMapper<Mass> mm;
	@Mapper ComponentMapper<Velocity> vm;

	private Stage ui;
	private Window table;
	private static int debug = 0;

	private TextField mass, radius, color;
	private TextFields velocity, acceleration, position;
	private Entity selectedPlanet;

	public UISystem() {
		ui = createUI();
	}

	@Override
	protected void initialize() {
		world.getSystem(InputSystem.class).addListener(this);
	}

	@Override
	protected void begin() {}

	@Override
	protected void processSystem() {
		refreshUI();
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

		table = new Window("Planet Parameters", skin);
		table.align(Align.center | Align.top);
		table.setSize(200, height);
		table.setPosition(-width / 2, -height / 2);
		stage.addActor(table);

		mass = addField("Mass", table, skin);
		radius = addField("Radius", table, skin);
		color = addField("Color", table, skin);
		velocity = addField2("Velocity", table, skin);
		acceleration = addField2("Acceleration", table, skin);
		position = addField2("Position", table, skin);
		
		addFieldEnterListeners();
		
		acceleration.x.setDisabled(true);
		acceleration.y.setDisabled(true);
		
		Table buttonTable = new Table(skin);
		table.add(buttonTable).expandX().fillX().row();

		TextButton remove = addButton("Remove", buttonTable, skin);
		remove.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				selectedPlanet.deleteFromWorld();
			}
		});

		TextButton copy = addButton("Copy", buttonTable, skin);
		copy.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				copyFieldText(mass);
				copyFieldText(radius);
				copyFieldText(color);
				copyFieldText(velocity.x);
				copyFieldText(velocity.y);
				copyFieldText(position.x);
				copyFieldText(position.y);
			}
		});

		TextButton clear = addButton("Clear", buttonTable, skin);
		clear.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				clearUI();
				planetSelectionChanged(null);
			}
		});

		return stage;
	}

	private TextButton addButton(String name, Table table, Skin skin) {
		TextButton button = new TextButton(name, skin);
		table.add(button);
		return button;
	}

	private TextField addField(String name, Table table, Skin skin) {
		Label label = new Label(name, skin);
		TextField field = new TextField("", skin);
		addTextFieldListener(field);
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
		fields.x = new TextField("", skin);
		fields.y = new TextField("", skin);
		addTextFieldListener(fields.x);
		addTextFieldListener(fields.y);

		table.add(label).colspan(2);
		table.row();
		table.add(labelX).padRight(10).padLeft(5);
		table.add(fields.x).expandX().fillX();
		table.row();
		table.add(labelY).padRight(10).padLeft(5);;
		table.add(fields.y).fillX();
		table.row();
		return fields;
	}

	private void addTextFieldListener(final TextField f) {
		f.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					copyFieldText(f);
				}
			}
		});
	}
	
	private void addTextFieldEnterListener(final TextField f, final Runnable callback){
		if(f == null || callback == null){
			throw new NullPointerException();
		}
		
		f.addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if(keycode == Input.Keys.ENTER){
					if(selectedPlanet != null){
						callback.run();
					}
					return true;
				}
				return false;
			}
		});
	}
	
	private void addFieldEnterListeners(){
		addTextFieldEnterListener(mass, new Runnable() {
			@Override
			public void run() {
				Mass m = mm.get(selectedPlanet);
				float f =  readFloatFromField(mass);
				if(!Float.isNaN(f)){
					m.mass = f;
				}else{
					new RuntimeException().printStackTrace();
				}
			}
		});
		
		addTextFieldEnterListener(radius, new Runnable() {
			@Override
			public void run() {
				Size s = sm.get(selectedPlanet);
				float f =  readFloatFromField(radius);
				if(!Float.isNaN(f)){
					s.radius = f;
				}else{
					new RuntimeException().printStackTrace();
				}
			}
		});
		
		addTextFieldEnterListener(color, new Runnable() {
			@Override
			public void run() {
				Colour c = cm.get(selectedPlanet);
				String s = readStringFromField(color);
				if (s != null) {
					try{
						c.color = Color.valueOf(s);
					}catch(RuntimeException e){
						// TODO error message
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	private void copyFieldText(TextField f) {
		if (f.getText().equals("") && f.getMessageText() != null) {
			f.setText(f.getMessageText());
		}
	}

	private static class TextFields {
		TextField x, y;
	}

	@Override
	protected void end() {

	}

	// --input--

	private void d() {
		switch (debug) {
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

	private void refreshUI() {
		if (selectedPlanet != null) {
			Mass m = mm.get(selectedPlanet);
			Colour c = cm.get(selectedPlanet);
			Position p = pm.get(selectedPlanet);
			Size s = sm.get(selectedPlanet);
			Velocity v = vm.get(selectedPlanet);
			Acceleration a = am.get(selectedPlanet);

			mass.setMessageText("" + m.mass);
			radius.setMessageText("" + s.radius);
			color.setMessageText("" + c.color.toString());
			velocity.x.setMessageText("" + v.vec.x);
			velocity.y.setMessageText("" + v.vec.y);
			acceleration.x.setMessageText("" + a.vec.x);
			acceleration.y.setMessageText("" + a.vec.y);
			position.x.setMessageText("" + p.vec.x);
			position.y.setMessageText("" + p.vec.y);
		}
	}

	private String readStringFromField(TextField tf) {
		if (tf.getText() != null && !tf.getText().equals("")) {
			return tf.getText();
		} else if (tf.getMessageText() != null && !tf.getMessageText().equals("")) {
			return tf.getMessageText();
		} else {
			return null;
		}
	}

	private float readFloatFromField(TextField tf) {
		String s = readStringFromField(tf);
		if (s != null) {
			try{
				return Float.parseFloat(s);
			}catch(RuntimeException e){
				// TODO error message
				e.printStackTrace();
				return Float.NaN;
			}
		} else {
			return Float.NaN;
		}
	}

	private float readFloatFromField(TextField tf, float min, float max) {
		float f = readFloatFromField(tf);
		if (Float.isNaN(f)) {
			f = MathUtils.random(min, max);
		}
		return f;
	}

	public float getRadius() {
		float f = readFloatFromField(radius, 2f, 10f);
		return f;
	}

	public float getMass() {
		return readFloatFromField(mass, 10f, 100f);
	}

	public Color getColor() {
		String s = readStringFromField(color);
		if (s != null) {
			try{
				return Color.valueOf(s);
			}catch(RuntimeException e){
				// TODO error message
				e.printStackTrace();
				return Color.WHITE;
			}
		} else {
			return new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
		}
	}

	public Vector2 getVelocity() {
		return new Vector2(readFloatFromField(velocity.x, 0f, 0f), readFloatFromField(velocity.y, 0f, 0f));
	}

	public Vector2 getAcceleration() {
		return new Vector2(readFloatFromField(acceleration.x, 0f, 0f), readFloatFromField(acceleration.y, 0f, 0f));
	}
	
	private void clearUI(){
		mass.setText("");
		radius.setText("");
		color.setText("");
		velocity.x.setText("");
		velocity.y.setText("");
		acceleration.x.setText("");
		acceleration.y.setText("");
		position.x.setText("");
		position.y.setText("");
	}

	@Override
	public void planetSelectionChanged(Entity planet) {
		selectedPlanet = planet;

		ui.unfocusAll();

		if (planet != null) {
			refreshUI();
		} else {
			mass.setMessageText("");
			radius.setMessageText("");
			color.setMessageText("");
			velocity.x.setMessageText("");
			velocity.y.setMessageText("");
			acceleration.x.setMessageText("");
			acceleration.y.setMessageText("");
			position.x.setMessageText("");
			position.y.setMessageText("");

//			mass.setText("");
//			size.setText("");
//			velocity.x.setText("");
//			velocity.y.setText("");
//			acceleration.x.setText("");
//			acceleration.y.setText("");
		}

	}
}
