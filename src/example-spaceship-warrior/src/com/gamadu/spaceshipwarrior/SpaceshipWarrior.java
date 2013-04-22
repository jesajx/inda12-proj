package com.gamadu.spaceshipwarrior;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gamadu.spaceshipwarrior.utils.ImagePacker;

public class SpaceshipWarrior extends Game {
	public static final int FRAME_WIDTH = 1280;
	public static final int FRAME_HEIGHT = 900;
	
	@Override
	public void create() {
		setScreen(new GameScreen(this));
	}
	
	public static void main(String[] args) {
		ImagePacker.run();
		
		LwjglApplicationConfiguration lwjglApplicationConfiguration = new LwjglApplicationConfiguration();
		lwjglApplicationConfiguration.fullscreen = false;
		lwjglApplicationConfiguration.width = FRAME_WIDTH;
		lwjglApplicationConfiguration.height = FRAME_HEIGHT;
		lwjglApplicationConfiguration.useCPUSynch = false;
		lwjglApplicationConfiguration.vSyncEnabled = false;
		lwjglApplicationConfiguration.title = "Spaceship Warrior";
		new LwjglApplication(new SpaceshipWarrior(), lwjglApplicationConfiguration);
	}

}
