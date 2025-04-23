package de.instinct.eqlibgdxutils.rendering.ui.texture;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.texture.draw.TextureDrawMode;
import de.instinct.eqlibgdxutils.rendering.ui.texture.draw.TextureRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.load.ColorTextureLoader;
import de.instinct.eqlibgdxutils.rendering.ui.texture.load.TextureLoader;

public class TextureManager {
	
	private static TextureRenderer textureRenderer;
	private static TextureLoader textureLoader;
	private static ColorTextureLoader colorTextureLoader;
	
	public static void init() {
		textureRenderer = new TextureRenderer();
		textureLoader = new TextureLoader();
		colorTextureLoader = new ColorTextureLoader();
	}
	
	public static void draw(Texture texture, Rectangle bounds) {
		draw(texture, bounds, TextureDrawMode.NORMAL);
	}
	
	public static void draw(Texture texture, Rectangle bounds, TextureDrawMode drawMode) {
		textureRenderer.draw(texture, bounds, drawMode);
	}
	
	public static void draw(Texture texture, Rectangle rectangle, float alpha) {
		textureRenderer.draw(texture, rectangle, alpha);
	}
	
	public static Texture getTexture(String packageName, String key) {
		return textureLoader.getTexture(packageName, key);
	}
	
	public static Texture createTexture(Color color) {
		return colorTextureLoader.createTexture(color);
	}
	
	public static void dispose() {
		textureRenderer.dispose();
	}

}
