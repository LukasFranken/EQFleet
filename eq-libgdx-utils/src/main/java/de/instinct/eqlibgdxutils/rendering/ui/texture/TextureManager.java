package de.instinct.eqlibgdxutils.rendering.ui.texture;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.rendering.GlobalRenderingConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.texture.draw.TextureDrawMode;
import de.instinct.eqlibgdxutils.rendering.ui.texture.draw.TextureRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.load.ColorTextureLoader;
import de.instinct.eqlibgdxutils.rendering.ui.texture.load.TextureLoader;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;

public class TextureManager {
	
	private static TextureRenderer textureRenderer;
	private static TextureLoader textureLoader;
	private static ColorTextureLoader colorTextureLoader;
	private static Map<String, Texture> textures = new HashMap<>();
	
	public static void init() {
		textureRenderer = new TextureRenderer();
		textureLoader = new TextureLoader();
		colorTextureLoader = new ColorTextureLoader();
		String glowString = PreferenceUtil.load("glow");
		if (glowString.contentEquals("")) {
    		GlobalRenderingConfiguration.defaultGlowRadius = 0;
    	} else {
    		GlobalRenderingConfiguration.defaultGlowRadius = Integer.parseInt(glowString);
    	}
		Shapes.init();
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
		draw(key, GraphicsUtil.screenBounds(), 1f);
	}
	
	public static void draw(String key, float alpha) {
		draw(key,  GraphicsUtil.screenBounds(), alpha);
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

	public static void dispose(Texture texture) {
		for (String key : textures.keySet()) {
			if (textures.get(key) == texture) {
				texture.dispose();
				textures.remove(key);
				break;
			}
		}
	}

}
