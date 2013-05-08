package se.exuvo.planets.systems;

import se.exuvo.planets.components.Acceleration;
import se.exuvo.planets.components.Colour;
import se.exuvo.planets.components.Mass;
import se.exuvo.planets.components.Position;
import se.exuvo.planets.components.Size;
import se.exuvo.planets.components.Vector2Component;
import se.exuvo.planets.components.Velocity;
import se.exuvo.planets.systems.InputSystem.PlanetSelectionChanged;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.Bag;
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

	private Label massLabel, radiusLabel;
	private TextField mass, radius, color;
	private TextFields velocity, acceleration, position;
	private Bag<Entity> selectedPlanets;

	public UISystem() {
		ui = createUI();
	}

	// TODO extra: make tab and shift-tab go back and forth between fields in a cycle.
	// TODO shift-tab doesn't work
	// TODO tab stops working near the Acceleration-fields (disabled textfield)
	// TODO remove/change debug hotkey-bindings. (A and Z)

	@Override
	protected void initialize() {
		world.getSystem(InputSystem.class).addListener(this);
		selectedPlanets = new Bag<Entity>();
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

		mass = addField(massLabel = new Label("Mass", skin), table, skin);
		radius = addField(radiusLabel = new Label("Radius", skin), table, skin);
		color = addField("Color", table, skin);
		velocity = addField2("Velocity", table, skin);
		position = addField2("Position", table, skin);
		acceleration = addField2("Acceleration", table, skin);

		addFieldEnterListeners();
		addFieldChangeListeners();

		acceleration.x.setDisabled(true);
		acceleration.y.setDisabled(true);

		Table buttonTable = new Table(skin);
		table.add(buttonTable).expandX().fillX().row();

		TextButton remove = addButton("Remove", buttonTable, skin);
		remove.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (Entity e : selectedPlanets) {
					e.deleteFromWorld();
				}
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
		return addField(new Label(name, skin), table, skin);
	}
	
	private TextField addField(Label label, Table table, Skin skin) {
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

	private void addTextFieldEnterListener(final TextField f, final Runnable callback) {
		if (f == null || callback == null) {
			throw new NullPointerException();
		}
		f.setFocusTraversal(true);

		f.addListener(new InputListener() { // TODO TextField.TextFieldListener?
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER) {
					if (selectedPlanets != null) {
						callback.run();
					}
					return true;
				}
				return false;
			}
		});
	}

	// TODO make beautiful and shorter
	private void addFieldEnterListeners() {
		addTextFieldEnterListener(mass, new Runnable() {
			@Override
			public void run() {
				float f = readFloatFromField(mass);
				if (!Float.isNaN(f)) {
					for (Entity e : selectedPlanets) {
						Mass m = mm.get(e);
						m.mass = f;
					}
				}
			}
		});

		addTextFieldEnterListener(velocity.x, new Runnable() {
			@Override
			public void run() {
				float f = readFloatFromField(velocity.x);
				if (!Float.isNaN(f)) {
					for (Entity e : selectedPlanets) {
						Velocity v = vm.get(e);
						v.vec.x = f;
					}
				}
			}
		});

		addTextFieldEnterListener(velocity.y, new Runnable() {
			@Override
			public void run() {
				float f = readFloatFromField(velocity.y);
				if (!Float.isNaN(f)) {
					for (Entity e : selectedPlanets) {
						Velocity v = vm.get(e);
						v.vec.y = f;
					}
				}
			}
		});

		addTextFieldEnterListener(position.x, new Runnable() {
			@Override
			public void run() {
				float f = readFloatFromField(position.x);
				if (!Float.isNaN(f)) {
					for (Entity e : selectedPlanets) {
						Position p = pm.get(e);
						p.vec.x = f;
					}
				}
			}
		});

		addTextFieldEnterListener(position.y, new Runnable() {
			@Override
			public void run() {
				float f = readFloatFromField(position.y);
				if (!Float.isNaN(f)) {
					for (Entity e : selectedPlanets) {
						Position p = pm.get(e);
						p.vec.y = f;
					}
				}
			}
		});

		addTextFieldEnterListener(radius, new Runnable() {
			@Override
			public void run() {
				float f = readFloatFromField(radius);
				if (!Float.isNaN(f)) {
					for (Entity e : selectedPlanets) {
						Size s = sm.get(e);
						s.radius = f;
					}
				}
			}
		});

		addTextFieldEnterListener(color, new Runnable() {
			@Override
			public void run() {
				String s = readStringFromField(color);
				if (s != null) {
					try {
						Color co = Color.valueOf(s);
						for (Entity e : selectedPlanets) {
							Colour c = cm.get(e);
							c.color = co;
						}
					} catch (RuntimeException ignore) {}
				}
			}
		});
	}

	private void addTextFieldFloatValidator(final TextField f) {
		f.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				String s = readStringFromField(f);
				try {
					if (s != null) {
						Float.parseFloat(s);
					}
					f.setColor(Color.WHITE);
				} catch (RuntimeException e) {
					f.setColor(Color.RED);
				}
				return false;
			}
		});
	}

	private void addTextFieldColorValidator(final TextField f) {
		f.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				String s = readStringFromField(f);
				try {
					if (s != null) {
						Color.valueOf(s);
					}
					f.setColor(Color.WHITE);
				} catch (RuntimeException e) {
					f.setColor(Color.RED);
				}
				return false;
			}
		});
	}

	private void addFieldChangeListeners() {
		addTextFieldFloatValidator(mass);
		addTextFieldFloatValidator(radius);
		addTextFieldFloatValidator(velocity.x);
		addTextFieldFloatValidator(velocity.y);
		addTextFieldFloatValidator(position.x);
		addTextFieldFloatValidator(position.y);
		addTextFieldColorValidator(color);
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
		if (keycode == Input.Keys.SPACE) {
			return false;
		}
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
		if (keycode == Input.Keys.SPACE) {
			return false;
		}
		if (ui.keyUp(keycode)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char c) {
		if (c == ' ') {
			return false;
		}
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

	private float massSum() {
		float sum = 0;

		for (Entity e : selectedPlanets) {
			sum += mm.get(e).mass;
		}

		return sum;
	}

	private void refreshUI() {
		if (!selectedPlanets.isEmpty()) {
			if (selectedPlanets.size() == 1) {
				Colour c = cm.get(selectedPlanets.get(0));
				color.setMessageText("" + c.color.toString());
			}else{
				color.setMessageText("");
			}
			
			if (selectedPlanets.size() == 1) {
				Size s = sm.get(selectedPlanets.get(0));
				radius.setMessageText("" + s.radius);
			} else if (selectedPlanets.size() == 2) {
				radius.setMessageText("" + pm.get(selectedPlanets.get(0)).vec.dst(pm.get(selectedPlanets.get(1)).vec));
			} else {
				radius.setMessageText("");
			}

			float massSum = massSum();
			Vector2 pos = Vector2Component.mean(pm, selectedPlanets);
			Vector2 vel = Vector2Component.mean(vm, selectedPlanets);
			Vector2 acc = Vector2Component.mean(am, selectedPlanets);

			mass.setMessageText("" + massSum);
			velocity.x.setMessageText("" + vel.x);
			velocity.y.setMessageText("" + vel.y);
			acceleration.x.setMessageText("" + acc.x);
			acceleration.y.setMessageText("" + acc.y);
			position.x.setMessageText("" + pos.x);
			position.y.setMessageText("" + pos.y);
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
			try {
				return Float.parseFloat(s);
			} catch (RuntimeException e) {
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
		float f = readFloatFromField(radius, 4f, 15f);
		return f;
	}

	public float getMass() {
		return readFloatFromField(mass, 1E8f, 1E12f);
	}

	public Color getColor() {
		String s = readStringFromField(color);
		if (s != null) {
			try {
				return Color.valueOf(s);
			} catch (RuntimeException e) {
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

	private void clearUI() {
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
	public void planetSelectionChanged(Bag<Entity> planets) {
		selectedPlanets = planets;
		
		if(selectedPlanets.size() <= 1){
			massLabel.setText("Mass");
			radiusLabel.setText("Radius");
		}else{
			massLabel.setText("Mass Sum");
			if(selectedPlanets.size() == 2){
				radiusLabel.setText("Distance");
			}else{
				radiusLabel.setText("Radius");
			}
		}

		ui.unfocusAll();

		if (!planets.isEmpty()) {
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
		}

	}
}
