package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class ColorScaleLoader {
	
	private Map<ColorScale, List<Color>> colorSchemes;
	
	public ColorScaleLoader() {
		colorSchemes = new HashMap<>();
		colorSchemes.put(ColorScale.GREEN_TO_RED, loadGreenToRed());
	}
	
	public Texture load(ColorScale colorScheme, double value) {
		return TextureManager.createTexture(getColor(colorSchemes.get(colorScheme), value));
	}

	private Color getColor(List<Color> list, double value) {
        return list.get((int) Math.round((1 - Math.max(0, Math.min(1, value))) * (list.size() - 1)));
	}
	
	private List<Color> loadGreenToRed() {
	    List<Color> colors = new ArrayList<>();
	    
	    // Extended Green region
	    colors.add(new Color(0.0f, 1.0f, 0.0f, 1f));  // Green
	    colors.add(new Color(0.1f, 0.95f, 0.0f, 1f)); // Slightly lighter green
	    colors.add(new Color(0.2f, 0.9f, 0.0f, 1f));  // Lighter green
	    colors.add(new Color(0.22f, 0.9f, 0.0f, 1f));  // Yellowish lighter green
	    colors.add(new Color(0.3f, 0.85f, 0.0f, 1f)); // Yellowish green
	    colors.add(new Color(0.35f, 0.8f, 0.0f, 1f)); // Transition to yellow-green
	    colors.add(new Color(0.4f, 0.75f, 0.0f, 1f)); // Greenish yellow
	    colors.add(new Color(0.45f, 0.7f, 0.0f, 1f)); // Yellow-green

	    // Yellow transition
	    colors.add(new Color(0.45f, 0.65f, 0.0f, 1f)); // Yellowish-green
	    colors.add(new Color(0.5f, 0.6f, 0.0f, 1f));  // Yellowish
	    colors.add(new Color(0.6f, 0.5f, 0.0f, 1f));  // Yellow-orange

	    // Compressed Red region
	    colors.add(new Color(0.7f, 0.4f, 0.0f, 1f));  // Orange
	    colors.add(new Color(0.8f, 0.3f, 0.0f, 1f));  // Orange-red
	    colors.add(new Color(0.9f, 0.2f, 0.0f, 1f));  // Dark Orange
	    colors.add(new Color(1.0f, 0.1f, 0.0f, 1f));  // Red
	    colors.add(new Color(0.8f, 0.0f, 0.0f, 1f));  // Dark Red
	    colors.add(new Color(0.6f, 0.0f, 0.0f, 1f));  // Darker Red
	    colors.add(new Color(0.4f, 0.0f, 0.0f, 1f));  // Darkest Red
	    
	    return colors;
	}


}
