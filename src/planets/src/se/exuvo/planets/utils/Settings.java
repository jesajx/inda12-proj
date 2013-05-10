package se.exuvo.planets.utils;


import com.martiansoftware.jsap.JSAPResult;

public class Settings extends se.exuvo.settings.Settings{
	
	public Settings(JSAPResult conf){
		add("loglvl", "INFO");
		add("GUI.FrameLimit",60);
		add("GUI.Width",1024);
		add("GUI.Height",768);
		add("GUI.Fullscreen",false);
		add("GUI.VSync",false);
		add("GUI.CPUSync",false);
		add("GUI.ShowFPS", true);
		add("SoundVolume",1f);
		add("MusicVolume",0.8f);
		add("PhysicsStep", 0.1f);
		add("pauseWhenCreatingPlanets", true);
		add("PrecognitionSteps", 500);
		add("zoomSensitivity", 1.25f);
		add("moveMouseSensitivity", 10f);
		add("moveDelay", 200);
		add("pushForceMultiplier", 0.1f);
		add("PrecognitionMaxVisualPlanets", 10);
		
		if(!start(conf,"planets")){
			log.fatal("Failed to read settings from file, please fix. Exiting.");
			System.exit(2);
		}
		
		logger.reloadLogLvl();
	}
	

	
}
