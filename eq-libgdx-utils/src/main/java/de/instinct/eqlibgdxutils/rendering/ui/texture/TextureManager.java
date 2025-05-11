package de.instinct.eqlibgdxutils.rendering.ui.texture;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
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
	private static Map<String, Texture> textures = new HashMap<>();
	
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
	
	public static void draw(Texture texture, Rectangle bounds, float alpha) {
		textureRenderer.draw(texture, bounds, alpha);
	}
	
	public static void draw(String key) {
		draw(key, new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}
	
	public static void draw(String key, Rectangle bounds) {
		textureRenderer.draw(textures.get(key), bounds, TextureDrawMode.NORMAL);
	}
	
	public static Texture getTexture(String packageName, String textureName) {
		String key = packageName + "/" + textureName;
		Texture texture = textures.get(key);
		if (texture == null) {
			texture = textureLoader.getTexture(packageName, key);
			textures.put(key, texture);
		}
		return texture;
	}
	
	public static void put(String key, Texture texture) {
		textures.put(key, texture);
	}
	
	public static Texture createTexture(Color color) {
		return colorTextureLoader.createTexture(color);
	}
	
	public static void dispose() {
		textureRenderer.dispose();
	}

}
