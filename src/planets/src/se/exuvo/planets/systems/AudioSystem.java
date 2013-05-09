package se.exuvo.planets.systems;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import se.exuvo.settings.Settings;

import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;

public class AudioSystem extends VoidEntitySystem {
	Logger log = Logger.getLogger(AudioSystem.class);
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private float soundVolume = Settings.getFloat("SoundVolume"), musicVolume = Settings.getFloat("MusicVolume");
	private Bag<Sound> coins = new Bag<Sound>(10);
	private Runnable doCoin, doLoad;
	private long lastSound;
	private Sound load;

	@Override
	protected void initialize() {
		FileHandle snd = Gdx.files.internal("resources/snd");
		FileHandle[] wavs = snd.list(".wav");

		for (FileHandle fh : wavs) {
			if (fh.nameWithoutExtension().contains("coin")) {
				coins.add(Gdx.audio.newSound(fh));
			}
		}
		
		load = Gdx.audio.newSound(Gdx.files.internal("resources/snd/spawn.wav"));

		if (soundVolume < 0 || soundVolume > 1) {
			log.error("Invalid sound volume  \"" + soundVolume + "\", should be between 0 and 1");
		}
		
		if (musicVolume < 0 || musicVolume > 1) {
			log.error("Invalid music volume  \"" + musicVolume + "\", should be between 0 and 1");
		}

		doCoin = new Runnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - lastSound > 100) {
					coins.get(MathUtils.random(coins.size() - 1)).play(soundVolume, MathUtils.random(0.7f, 1.3f), MathUtils.random(-1f, 1f));
					lastSound = System.currentTimeMillis();
				}
			}
		};
		
		doLoad = new Runnable() {
			@Override
			public void run() {
					load.play(soundVolume);
			}
		};
	}

	@Override
	protected void processSystem() {}

	public void playCoin() {
		executor.submit(doCoin);
	}
	
	public void playLoad(){
		executor.submit(doLoad);
	}

}
