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

// TODO will this class be necessary? only prints text we will likely remove.
public class HudRenderSystem extends VoidEntitySystem {

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private BitmapFont font;
	private boolean fps;
	private InputSystem insys;
	private TextBounds pauseBounds, speedBounds;
	private String pause = "Paused", speed1 = "speed x10", speed2 = "speed x50", speed3 = "speed x200";

	public HudRenderSystem() {
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	protected void initialize() {
		TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("resources/fonts.atlas"));

		AtlasRegion fontRegion = textureAtlas.findRegion("normal.png");
		font = new BitmapFont(Gdx.files.internal("resources/src/fonts/normal.fnt"), fontRegion, false);
		font.setUseIntegerPositions(false);

		batch = new SpriteBatch();

		fps = Settings.getBol("GUI.ShowFPS");

		insys = world.getSystem(InputSystem.class);
		pauseBounds = font.getBounds(pause);
		speedBounds = font.getBounds(speed1);
	}

	@Override
	protected void begin() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
	}

	@Override
	protected void processSystem() {
		batch.setColor(1, 1, 1, 1);
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
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
		
		if (insys.isSpeedup()) {
			if (insys.isSSpeedup()){
				font.draw(batch, speed3, -speedBounds.width / 4, -height / 2 + 2 * speedBounds.height);
			}else{
				font.draw(batch, speed1, -speedBounds.width / 4, -height / 2 + 2 * speedBounds.height);
			}
		}else if (insys.isSSpeedup()){
			font.draw(batch, speed2, -speedBounds.width / 4, -height / 2 + 2 * speedBounds.height);
		}
	}

	@Override
	protected void end() {
		batch.end();
	}

}
