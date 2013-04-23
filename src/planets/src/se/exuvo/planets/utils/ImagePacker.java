package se.exuvo.planets.utils;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;;

public class ImagePacker {

	public static void run() {
		String path1 = "resources/src/textures";
		if(new File(path1).exists()){
			TexturePacker2.process(path1, "resources", "textures");
		}
        
        Settings s = new Settings();
		s.filterMin = TextureFilter.Linear;
		s.filterMag = TextureFilter.MipMapLinearLinear;
		TexturePacker2.process(s, "resources/src/fonts", "resources", "fonts");
	}

}
