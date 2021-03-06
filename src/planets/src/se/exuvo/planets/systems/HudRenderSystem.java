package se.exuvo.planets.systems;

import se.exuvo.settings.Settings;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

// TODO will this class be necessary? only prints text we will likely remove.
public class HudRenderSystem extends VoidEntitySystem {

	private ShapeRenderer shape;
	private SpriteBatch batch;
	private OrthographicCamera camera, gameCamera;
	public BitmapFont font;
	private boolean fps;
	private InputSystem insys;
	private TextBounds pauseBounds, speedBounds, followBounds;
	private String pause = "Paused", speed1 = "speed x10", speed2 = "speed x50", speed3 = "speed x200", follow = "Tracking";

	public HudRenderSystem(OrthographicCamera camera) {
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gameCamera = camera;
	}

	@Override
	protected void initialize() {
		TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("resources/fonts.atlas"));

		AtlasRegion fontRegion = textureAtlas.findRegion("normal.png");
		font = new BitmapFont(Gdx.files.internal("resources/src/fonts/normal.fnt"), fontRegion, false);
		font.setUseIntegerPositions(false);

		batch = new SpriteBatch();
		shape = new ShapeRenderer();

		fps = Settings.getBol("GUI.ShowFPS");

		insys = world.getSystem(InputSystem.class);
		pauseBounds = font.getBounds(pause);
		speedBounds = font.getBounds(speed1);
		followBounds = font.getBounds(follow);
	}

	@Override
	protected void begin() {
		batch.setProjectionMatrix(camera.combined);
		shape.setProjectionMatrix(camera.combined);
	}

	@Override
	protected void processSystem() {
		batch.setColor(1, 1, 1, 1);
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		batch.begin();

		if (fps) {
			int x = -(width / 2) + 220;
			font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), x, height / 2 - 20);
			font.draw(batch, "Active entities: " + world.getEntityManager().getActiveEntityCount(), x, height / 2 - 40);
			font.draw(batch, "Total created: " + world.getEntityManager().getTotalCreated(), x, height / 2 - 60);
			font.draw(batch, "Total deleted: " + world.getEntityManager().getTotalDeleted(), x, height / 2 - 80);
		}

		if (insys.isPaused()) {
			font.draw(batch, pause, -pauseBounds.width / 4, -height / 2 + 2 * pauseBounds.height);
		}

		if (insys.isFollow()) {
			font.draw(batch, follow, -followBounds.width / 4, +height / 2 - 2 * followBounds.height);
		}

		if (insys.isSpeedup()) {
			if (insys.isSSpeedup()) {
				font.draw(batch, speed3, -speedBounds.width / 4, -height / 2 + 2 * speedBounds.height);
			} else {
				font.draw(batch, speed1, -speedBounds.width / 4, -height / 2 + 2 * speedBounds.height);
			}
		} else if (insys.isSSpeedup()) {
			font.draw(batch, speed2, -speedBounds.width / 4, -height / 2 + 2 * speedBounds.height);
		}
		
		int len = 50;
		int h = 10;
		int x = width / 2 - 20 - len;
		int y = -height / 2 + 10;
		
		String zoomText = "" + (long)(gameCamera.zoom*50);
		TextBounds zoomBounds = font.getBounds(zoomText);
		font.draw(batch, zoomText, x+len+10-zoomBounds.width, y+h+20);

		batch.end();

		shape.begin(ShapeType.Line);
		shape.line(x, y, x + len, y);
		shape.line(x, y, x, y + h);
		shape.line(x + len, y, x + len, y + h);
		shape.end();
	}

	public int mouseX() {
		return Gdx.input.getX() - Gdx.graphics.getWidth() / 2;
	}

	public int mouseY() {
		return -Gdx.input.getY() + Gdx.graphics.getHeight() / 2;
	}

	@Override
	protected void end() {}
	
	public void resize(int width, int height) {
//		camera.setToOrtho(false, width, height);
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

}
