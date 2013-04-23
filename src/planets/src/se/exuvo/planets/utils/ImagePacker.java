package se.exuvo.planets.utils;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;;

public class ImagePacker {

	public static void run() {
        TexturePacker2.process("resources/src/textures", "resources", "textures");
        
        Settings s = new Settings();
		s.filterMin = TextureFilter.Linear;
		s.filterMag = TextureFilter.MipMapLinearLinear;
		TexturePacker2.process(s, "resources/src/fonts", "resources", "fonts");
	}

}
