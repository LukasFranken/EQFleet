package de.instinct.eqlibgdxutils.rendering.ui.texture.load;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class ColorTextureLoader {
	
	private Map<Color, Texture> colorTextures = new HashMap<>();
	
	public Texture createTexture(Color color) {
    	if (colorTextures.containsKey(color)) {
    		return colorTextures.get(color);
    	} else {
    		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(color);
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            pixmap.dispose();
            colorTextures.put(color, texture);
            return texture;
    	}
    }
	
}
