package com.gamadu.spaceshipwarrior.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GdxUtils {

	public static final void drawCentered(SpriteBatch batch, TextureRegion region, float x, float y) {
		batch.draw(region, x - region.getRegionWidth() / 2f, y - region.getRegionHeight() / 2f);
	}
	
	public static final void drawCentered(SpriteBatch batch, TextureRegion region, float x, float y, float rotationInDegrees) {
		batch.draw(region, x - region.getRegionWidth() / 2f, y - region.getRegionHeight() / 2f, region.getRegionWidth() / 2f, region.getRegionHeight() / 2f, region.getRegionWidth(), region.getRegionHeight(), 1, 1, rotationInDegrees);
	}

	public static final void drawCentered(SpriteBatch batch, TextureRegion region, float x, float y, float offsetX, float offsetY, float rotationInDegrees) {
		float posX = x - region.getRegionWidth() / 2f;
		float posY = y - region.getRegionHeight() / 2f;
		float originX = region.getRegionWidth() / 2f + offsetX;
		float originY = region.getRegionHeight() / 2f + offsetY;
		
		batch.draw(region, posX, posY, originX, originY, region.getRegionWidth(), region.getRegionHeight(), 1, 1, rotationInDegrees);
	}

	public static final void drawCentered(SpriteBatch batch, TextureRegion region, float x, float y, float rotationInDegrees, float scale) {
		batch.draw(region, 
				x - region.getRegionWidth() / 2f, 
				y - region.getRegionHeight() / 2f, 
				region.getRegionWidth() / 2f, 
				region.getRegionHeight() / 2f, 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				scale, 
				scale, 
				rotationInDegrees);
	}

	public static void drawCentered(SpriteBatch batch, Texture texture, float x, float y) {
		batch.draw(texture, x - texture.getWidth() / 2f, y - texture.getHeight() / 2f);
	}
	
	public static void drawCentered(SpriteBatch batch, Texture texture, float x, float y, float rotationInDegrees) {
		batch.draw(texture, x - texture.getWidth() / 2f, y - texture.getHeight() / 2f, texture.getWidth() / 2f, texture.getHeight() / 2f, texture.getWidth(), texture.getHeight(), 1, 1, rotationInDegrees, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
	}
}
