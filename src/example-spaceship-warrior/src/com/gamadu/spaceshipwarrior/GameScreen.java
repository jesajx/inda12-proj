package com.gamadu.spaceshipwarrior;

import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gamadu.spaceshipwarrior.systems.CollisionSystem;
import com.gamadu.spaceshipwarrior.systems.ColorAnimationSystem;
import com.gamadu.spaceshipwarrior.systems.ExpiringSystem;
import com.gamadu.spaceshipwarrior.systems.EntitySpawningTimerSystem;
import com.gamadu.spaceshipwarrior.systems.HealthRenderSystem;
import com.gamadu.spaceshipwarrior.systems.HudRenderSystem;
import com.gamadu.spaceshipwarrior.systems.MovementSystem;
import com.gamadu.spaceshipwarrior.systems.ParallaxStarRepeatingSystem;
import com.gamadu.spaceshipwarrior.systems.PlayerInputSystem;
import com.gamadu.spaceshipwarrior.systems.RemoveOffscreenShipsSystem;
import com.gamadu.spaceshipwarrior.systems.ScaleAnimationSystem;
import com.gamadu.spaceshipwarrior.systems.SpriteRenderSystem;

public class GameScreen implements Screen {
	private Game game;
	private World world;
	private OrthographicCamera camera;
	
	private SpriteRenderSystem spriteRenderSystem;
	private HealthRenderSystem healthRenderSystem;
	private HudRenderSystem hudRenderSystem;

	public GameScreen(Game game) {
		this.game = game;
		this.camera = new OrthographicCamera(SpaceshipWarrior.FRAME_WIDTH, SpaceshipWarrior.FRAME_HEIGHT);
		
		world = new World();

		world.setManager(new GroupManager());

		world.setSystem(new MovementSystem());
		world.setSystem(new PlayerInputSystem(camera));
		world.setSystem(new CollisionSystem());
		world.setSystem(new ExpiringSystem());
		world.setSystem(new EntitySpawningTimerSystem());
		world.setSystem(new ParallaxStarRepeatingSystem());
		world.setSystem(new ColorAnimationSystem());
		world.setSystem(new ScaleAnimationSystem());
		world.setSystem(new RemoveOffscreenShipsSystem());

		spriteRenderSystem = world.setSystem(new SpriteRenderSystem(camera), true);
		healthRenderSystem = world.setSystem(new HealthRenderSystem(camera), true);
		hudRenderSystem = world.setSystem(new HudRenderSystem(camera), true);

		world.initialize();

		EntityFactory.createPlayer(world, 0, 0).addToWorld();
		
		for(int i = 0; 500 > i; i++) {
			EntityFactory.createStar(world).addToWorld();
		}
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();

		world.setDelta(delta);
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			for(int i = 0; 10 > i; i++) {
				world.process();
			}
		}
		world.process();
		
		spriteRenderSystem.process();
		healthRenderSystem.process();
		hudRenderSystem.process();
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
