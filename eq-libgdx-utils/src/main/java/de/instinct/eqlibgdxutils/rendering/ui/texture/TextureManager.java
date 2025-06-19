package de.instinct.eqlibgdxutils.rendering.ui.texture;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.draw.TextureDrawMode;
import de.instinct.eqlibgdxutils.rendering.ui.texture.draw.TextureRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.load.ColorTextureLoader;
import de.instinct.eqlibgdxutils.rendering.ui.texture.load.TextureLoader;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.GlowShapeRenderer;

public class TextureManager {
	
	private static TextureRenderer textureRenderer;
	private static TextureLoader textureLoader;
	private static ColorTextureLoader colorTextureLoader;
	private static GlowShapeRenderer glowShapeRenderer;
	private static Map<String, Texture> textures = new HashMap<>();
	
	public static void init() {
		textureRenderer = new TextureRenderer();
		textureLoader = new TextureLoader();
		colorTextureLoader = new ColorTextureLoader();
		glowShapeRenderer = new GlowShapeRenderer();
		String glowString = PreferenceUtil.load("glow");
    	if (glowString.contentEquals("")) {
    		TextureManager.setDefaultGlowRadius(30);
    	} else {
    		TextureManager.setDefaultGlowRadius(Float.parseFloat(glowString));
    	}
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
		draw(key, new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), 1f);
	}
	
	public static void draw(String key, float alpha) {
		draw(key, new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), alpha);
	}
	
	public static void draw(String key, Rectangle bounds, float alpha) {
		textureRenderer.draw(textures.get(key), bounds, alpha);
	}
	
	public static Texture getTexture(String packageName, String textureName) {
		String key = packageName + "/" + textureName;
		Texture texture = textures.get(key);
		if (texture == null) {
			texture = textureLoader.getTexture(packageName, textureName);
			textures.put(key, texture);
		}
		return texture;
	}
	
	public static Texture getLoadedTexture(String tag) {
		return textures.get(tag);
	}
	
	public static void put(String key, Texture texture) {
		textures.put(key, texture);
	}
	
	public static Texture createTexture(Color color) {
		return colorTextureLoader.createTexture(color);
	}
	
	public static void createTexture(String tag, Color color) {
		textures.put(tag, colorTextureLoader.createTexture(color));
	}
	
	public static void setDefaultGlowRadius(float radius) {
		glowShapeRenderer.radius = radius;
	}
	
	public static void createShapeTexture(String tag, ComplexShapeType type, Rectangle bounds, Color color) {
		createShapeTexture(tag, type, bounds, color, glowShapeRenderer.defaultGlow);
	}
	
	public static void createShapeTexture(String tag, ComplexShapeType type, Rectangle bounds, Color color, float glow) {
		switch (type) {
		case RECTANGLE:
			
			break;
		case ROUNDED_RECTANGLE:
			put(tag, glowShapeRenderer.getGlowTexture(bounds, color, glow));
			break;
		case CIRCTANGLE:
			
			break;
		case CIRCLE:
			
			break;
		}
	}
	
	public static void dispose() {
		textureRenderer.dispose();
		for (Texture texture : textures.values()) {
			texture.dispose();
		}
	}
	
	public static void dispose(String key) {
		Texture texture = textures.get(key);
		if (texture != null) {
			texture.dispose();
			textures.remove(key);
		}
	}

}
