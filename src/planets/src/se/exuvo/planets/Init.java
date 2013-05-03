package se.exuvo.planets;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import se.exuvo.planets.utils.ImagePacker;
import se.exuvo.planets.utils.Settings;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

/**
 * Class responsible for the main-method which starts the game.
 * 
 * @see Planets
 */
public class Init {
	protected static final Logger log = Logger.getLogger(Init.class);
	public static final long serialVersionUID = 1;;
	public static JSAPResult config = null;
	public static ShutDownHook shutdownhook = new ShutDownHook();

	/**
	 * @param args
	 * @return 1=Invalid Params, 2=Invalid Config,
	 */
	public static void main(String[] args) {
		DOMConfigurator.configure("log4j.xml");
		log.fatal("### Starting ###");

		JSAP jsap = new JSAP();
		arguments(jsap);

		config = jsap.parse(args);
		// check whether the command line was valid, and if it wasn't, display usage information and exit.
		if (!config.success()) {
			System.err.println();
			// print out specific error messages describing the problems
			// with the command line, THEN print usage, THEN print full
			// help. This is called "beating the user with a clue stick."
			for (Iterator<?> errs = config.getErrorMessageIterator(); errs.hasNext();) {
				System.err.println("Error: " + errs.next());
			}

			System.err.println();
			System.err.println("Usage: java " + Init.class.getName());
			System.err.println("                " + jsap.getUsage());
			System.err.println("All parameters override config settings");
			System.err.println();
			// show full help as well
			System.err.println(jsap.getHelp());
			System.exit(1);
		}

		new Settings(config);
		Runtime.getRuntime().addShutdownHook(shutdownhook);

//        if(config.getBoolean("test")){
//        }

		// compress images.
		ImagePacker.run();

		// create windows and main game-screen (which happens to manage the mainloop)
		LwjglApplicationConfiguration lwjglApplicationConfiguration = new LwjglApplicationConfiguration();
		lwjglApplicationConfiguration.fullscreen = Settings.getBol("GUI.Fullscreen");
		lwjglApplicationConfiguration.width = Settings.getInt("GUI.Width");
		lwjglApplicationConfiguration.height = Settings.getInt("GUI.Height");
		lwjglApplicationConfiguration.useCPUSynch = Settings.getBol("GUI.CPUSync");
		lwjglApplicationConfiguration.vSyncEnabled = Settings.getBol("GUI.VSync");
		lwjglApplicationConfiguration.title = "Planets";
		lwjglApplicationConfiguration.resizable = true;
		lwjglApplicationConfiguration.useGL20 = true;
		new LwjglApplication(new Planets(), lwjglApplicationConfiguration);
	}

	private static final void arguments(JSAP jsap) {
		Switch fscreen = new Switch("fullscreen").setShortFlag('f').setLongFlag("fullscreen");
		fscreen.setHelp("Run in fullscreen.");

		try {
			jsap.registerParameter(fscreen);
		} catch (JSAPException e) {
			log.warn("JSAP: Failed to register parameters due to: " + e);
		}
	}
}

class ShutDownHook extends Thread {
	public ShutDownHook() {}

	public void run() {
		Settings.save();
	}
}
