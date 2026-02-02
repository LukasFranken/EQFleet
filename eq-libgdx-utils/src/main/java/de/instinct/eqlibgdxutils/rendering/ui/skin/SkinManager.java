package de.instinct.eqlibgdxutils.rendering.ui.skin;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class SkinManager {
	
	public static SkinColor skin;
	
	public static Color skinColor = new Color(0.8f, 0f, 0f, 1f);
	public static Color darkerSkinColor = new Color(0.6f, 0f, 0f, 1f);
	public static Color lighterSkinColor = new Color(0.9f, 0f, 0f, 1f);
	public static Color darkestSkinColor = new Color(0.3f, 0f, 0f, 1f);
	
	public static void init() {
		String skinString = PreferenceUtil.load("skin");
		skin = SkinColor.RED;
    	if (!skinString.contentEquals("")) {
    		try {
    			skin = SkinColor.valueOf(skinString);
    		} catch (Exception e) {
				Logger.log("SkinManager", "ERROR: Cannot parse skin value of: " + skinString + " to enum.", ConsoleColor.RED);
			}
    	}
		setValuesForSkinColor(skin.getColor());
	}
	
	public static void setSkinColor(SkinColor newSkin) {
		skin = newSkin;
		setValuesForSkinColor(newSkin.getColor());
	}

	private static void setValuesForSkinColor(Color color) {
		skinColor = new Color(color);
		darkerSkinColor = new Color(color.r * 0.7f, color.g * 0.7f, color.b * 0.7f, 1f);
		lighterSkinColor = new Color(color.r * 1.3f, color.g * 1.3f, color.b * 1.3f, 1f);
		darkestSkinColor = new Color(color.r * 0.4f, color.g * 0.4f, color.b * 0.4f, 1f);
	}

}
